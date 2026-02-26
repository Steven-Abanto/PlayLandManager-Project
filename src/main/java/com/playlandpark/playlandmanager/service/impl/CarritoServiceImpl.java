package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.carrito.CarritoDescuentoRequest;
import com.playlandpark.playlandmanager.model.dto.carrito.CarritoItemRequest;
import com.playlandpark.playlandmanager.model.dto.carrito.CarritoResponse;
import com.playlandpark.playlandmanager.model.dto.carritodetalle.CarritoDetalleResponse;
import com.playlandpark.playlandmanager.model.dto.summary.ProductoSummary;
import com.playlandpark.playlandmanager.model.dto.summary.UsuarioSummary;
import com.playlandpark.playlandmanager.model.entity.*;
import com.playlandpark.playlandmanager.repository.*;
import com.playlandpark.playlandmanager.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {

    private static final String ESTADO_ABIERTO = "ABIERTO";

    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository carritoDetalleRepository;

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PromocionRepository promocionRepository;

    // Regla: cada usuario solo puede tener un carrito ABIERTO.
    // Si el usuario ya tiene un carrito ABIERTO, se devuelve ese. Si no, se crea uno nuevo.
    @Override
    @Transactional
    public CarritoResponse getOrCreateActiveCart(Integer idUsuario) {
        if (idUsuario == null) throw new IllegalArgumentException("El idUsuario es obligatorio.");

        Carrito cart = carritoRepository.findFirstByUsuario_IdUsuarioAndEstado(idUsuario, ESTADO_ABIERTO)
                .orElseGet(() -> {
                    Usuario u = usuarioRepository.findById(idUsuario)
                            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));

                    Carrito c = new Carrito();
                    c.setUsuario(u);
                    c.setEstado(ESTADO_ABIERTO);
                    c.setFechaCreacion(LocalDateTime.now());
// Se comenta debido a problemas de sincronización con detalles
//                    c.setDetalles(new ArrayList<>());
                    return carritoRepository.save(c);
                });

        // Si ya existía carrito, valida caducidad de la promoción y recalcula por si acaso
        return recalculate(cart.getIdCarrito());
    }

    // Busca un carrito por su id
    @Override
    @Transactional(readOnly = true)
    public CarritoResponse findById(Integer idCarrito) {
        Carrito cart = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado: " + idCarrito));
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public CarritoResponse addItem(CarritoItemRequest request) {
        validateItemRequest(request);

        Carrito cart = getOpenCart(request.idCarrito());

        Producto product = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.idProducto()));

        // Regla: si el producto es físico, validar stock.
        // Si el producto es un servicio, no se valida stock.
        if (Boolean.FALSE.equals(product.getEsServicio())) {
            if (product.getStockAct() < request.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getIdProducto()
                        + " - " + product.getDescripcion());
            }
        }

        CarritoDetalle detail = carritoDetalleRepository
                .findByCarrito_IdCarritoAndProducto_IdProducto(cart.getIdCarrito(), product.getIdProducto())
                .orElse(null);

        if (detail == null) {
            detail = new CarritoDetalle();
            detail.setCarrito(cart);
            detail.setProducto(product);

            // Congelamos precio al momento de agregar
            detail.setPrecio(product.getPrecio());
            detail.setCantidad(request.cantidad());
            detail.setDescuento(BigDecimal.ZERO);

            BigDecimal base = detail.getPrecio().multiply(BigDecimal.valueOf(detail.getCantidad()));
            detail.setSubtotal(base);

            carritoDetalleRepository.save(detail);
        } else {
            detail.setCantidad(request.cantidad());

            if (Boolean.FALSE.equals(product.getEsServicio())) {
                if (product.getStockAct() < detail.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getIdProducto()
                            + " - " + product.getDescripcion());
                }
            }

            carritoDetalleRepository.save(detail);
        }

        return recalculate(cart.getIdCarrito());
    }

    // Actualiza la cantidad de un producto en el carrito
    @Override
    @Transactional
    public CarritoResponse updateItemQuantity(CarritoItemRequest request) {
        validateItemRequest(request);

        if (request.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }

        Carrito cart = getOpenCart(request.idCarrito());

        CarritoDetalle detail = carritoDetalleRepository
                .findByCarrito_IdCarritoAndProducto_IdProducto(cart.getIdCarrito(), request.idProducto())
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe en el carrito."));

        Producto product = detail.getProducto();

        if (Boolean.FALSE.equals(product.getEsServicio())) {
            if (product.getStockAct() < request.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getIdProducto());
            }
        }

        detail.setCantidad(request.cantidad());
        carritoDetalleRepository.save(detail);

        return recalculate(cart.getIdCarrito());
    }

    // Elimina un producto del carrito
    @Override
    @Transactional
    public CarritoResponse removeItem(Integer idCarrito, Integer idProducto) {
        if (idCarrito == null) throw new IllegalArgumentException("El idCarrito es obligatorio.");
        if (idProducto == null) throw new IllegalArgumentException("El idProducto es obligatorio.");

        Carrito cart = getOpenCart(idCarrito);

        CarritoDetalle detail = carritoDetalleRepository
                .findByCarrito_IdCarritoAndProducto_IdProducto(cart.getIdCarrito(), idProducto)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe en el carrito."));

        carritoDetalleRepository.delete(detail);

        return recalculate(cart.getIdCarrito());
    }

    // Aplica un código de promoción al carrito
    // Valida que el código exista y esté vigente
    @Override
    @Transactional
    public CarritoResponse applyPromotion(Integer idCarrito, CarritoDescuentoRequest request) {
        if (idCarrito == null) throw new IllegalArgumentException("El idCarrito es obligatorio.");
        if (request.codigoPromocion() == null || request.codigoPromocion().isBlank())
            throw new IllegalArgumentException("El código de promoción es obligatorio.");

        Carrito cart = getOpenCart(idCarrito);

        // Regla: si el carrito tiene >3 días, no se acepta promo
        if (isPromotionExpiredByCartAge(cart)) {
            clearPromotion(cart);
            throw new IllegalArgumentException("El carrito tiene más de 3 días. No se puede aplicar el código.");
        }

        Promocion promo = promocionRepository.findByCodigo(request.codigoPromocion())
                .orElseThrow(() -> new IllegalArgumentException("Código de promoción no encontrado: " + request.codigoPromocion()));

        validatePromotionVigency(promo);

        cart.setCodigoPromocion(promo.getCodigo());
        cart.setPromocion(promo);
        carritoRepository.save(cart);

        return recalculate(cart.getIdCarrito());
    }

    // Elimina el código de promoción del carrito
    @Override
    @Transactional
    public CarritoResponse removePromotion(Integer idCarrito) {
        Carrito cart = getOpenCart(idCarrito);
        clearPromotion(cart);
        return recalculate(cart.getIdCarrito());
    }

    // Recalcula los subtotales y descuentos de los detalles del carrito según la promoción vigente
    @Override
    @Transactional
    public CarritoResponse recalculate(Integer idCarrito) {
        Carrito cart = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado: " + idCarrito));

        // Si está abierto, aplicamos reglas. Si no, solo devolvemos (opcional).
        if (!ESTADO_ABIERTO.equalsIgnoreCase(cart.getEstado())) {
            return mapToResponse(cart);
        }

        // Regla: si tiene >3 días => borrar código/promo
        if (isPromotionExpiredByCartAge(cart)) {
            clearPromotion(cart);
        }

        Promocion promo = cart.getPromocion();
        if (promo != null) {
            // Si ya no está vigente, se borra
            if (!isPromotionVigent(promo)) {
                clearPromotion(cart);
                promo = null;
            }
        }

        List<CarritoDetalle> details = carritoDetalleRepository.findByCarrito_IdCarrito(cart.getIdCarrito());

        // Base sin descuentos ni promociones
        for (CarritoDetalle d : details) {
            BigDecimal base = d.getPrecio().multiply(BigDecimal.valueOf(d.getCantidad()));
            d.setDescuento(BigDecimal.ZERO);
            d.setSubtotal(base);
            d.setPromocion(null);
        }

        // Aplicar promo solo a elegibles y hasta montoMax
        if (promo != null) {
            Set<Integer> eligibleProductIds = new HashSet<>();
            promo.getProductos().forEach(p -> eligibleProductIds.add(p.getIdProducto()));

            BigDecimal remainingMax = promo.getMontoMax() != null ? promo.getMontoMax() : BigDecimal.ZERO;
            BigDecimal percent = promo.getPorcentaje() != null ? promo.getPorcentaje() : BigDecimal.ZERO;

            for (CarritoDetalle d : details) {
                if (remainingMax.compareTo(BigDecimal.ZERO) <= 0) break;

                Integer pid = d.getProducto().getIdProducto();
                if (!eligibleProductIds.contains(pid)) continue;

                BigDecimal base = d.getPrecio().multiply(BigDecimal.valueOf(d.getCantidad()));
                BigDecimal lineDiscount = base.multiply(percent);

                if (lineDiscount.compareTo(remainingMax) > 0) {
                    lineDiscount = remainingMax;
                }

                d.setDescuento(lineDiscount);
                d.setSubtotal(base.subtract(lineDiscount));
                d.setPromocion(promo);

                remainingMax = remainingMax.subtract(lineDiscount);
            }
        }

        carritoDetalleRepository.saveAll(details);

        return mapToResponse(cart);
    }

    // ---------------- Helpers ----------------
    // Valida que el carrito exista y esté en estado ABIERTO
    private Carrito getOpenCart(Integer idCarrito) {
        if (idCarrito == null) throw new IllegalArgumentException("El idCarrito es obligatorio.");

        Carrito cart = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado: " + idCarrito));

        if (!ESTADO_ABIERTO.equalsIgnoreCase(cart.getEstado())) {
            throw new IllegalArgumentException("El carrito no está en estado ABIERTO.");
        }
        return cart;
    }

    // Valida los datos para agregar o actualizar un producto en el carrito
    private void validateItemRequest(CarritoItemRequest request) {
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idCarrito() == null) throw new IllegalArgumentException("El idCarrito es obligatorio.");
        if (request.idProducto() == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        if (request.cantidad() == null) throw new IllegalArgumentException("La cantidad es obligatoria.");
        if (request.cantidad() <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
    }

    // Regla: si el carrito tiene más de 3 días desde su creación, se considera que la promoción ha expirado.
    private boolean isPromotionExpiredByCartAge(Carrito cart) {
        return cart.getFechaCreacion() != null &&
                cart.getFechaCreacion().plusDays(3).isBefore(LocalDateTime.now());
    }

    // Valida que la promoción esté activa y vigente (fechaInicio <= hoy <= fechaFin)
    private void validatePromotionVigency(Promocion promo) {
        if (!isPromotionVigent(promo)) {
            throw new IllegalArgumentException("La promoción no está vigente o está inactiva.");
        }
    }

    // Valida que la promoción esté activa y vigente (fechaInicio <= hoy <= fechaFin)
    private boolean isPromotionVigent(Promocion promo) {
        if (promo.getActivo() == null || !promo.getActivo()) return false;

        LocalDate today = LocalDate.now();
        if (promo.getFechaInicio() != null && today.isBefore(promo.getFechaInicio())) return false;
        if (promo.getFechaFin() != null && today.isAfter(promo.getFechaFin())) return false;

        return true;
    }

    // Elimina el código de promoción del carrito y recalcula sin promoción
    private void clearPromotion(Carrito cart) {
        cart.setCodigoPromocion(null);
        cart.setPromocion(null);
        carritoRepository.save(cart);
    }

    // Mapea un Carrito a su DTO de respuesta, incluyendo detalles y resumen de usuario/productos
    private CarritoResponse mapToResponse(Carrito cart) {
        Usuario u = cart.getUsuario();

        UsuarioSummary usuarioSummary = new UsuarioSummary(
                u.getIdUsuario(),
                u.getUsuario(),
                u.getRol(),
                u.getActivo()
        );

        var details = carritoDetalleRepository.findByCarrito_IdCarrito(cart.getIdCarrito());

        var detailResponses = details.stream().map(d -> {
            var p = d.getProducto();

            ProductoSummary productoSummary = new ProductoSummary(
                    p.getIdProducto(),
                    p.getDescripcion(),
                    p.getPrecio(),
                    p.getEsServicio(),
                    p.getActivo()
            );

            return new CarritoDetalleResponse(
                    d.getIdCarritoDetalle(),
                    productoSummary,
                    d.getPrecio(),
                    d.getCantidad(),
                    d.getDescuento(),
                    d.getSubtotal()
            );
        }).toList();

        return new CarritoResponse(
                cart.getIdCarrito(),
                usuarioSummary,
                cart.getEstado(),
                cart.getCodigoPromocion(),
                cart.getFechaCreacion(),
                detailResponses
        );
    }
}