package com.playlandpark.coreservice.ventas.service.impl;

import com.playlandpark.coreservice.client.auth.dto.UsuarioData;
import com.playlandpark.coreservice.client.catalogo.dto.ProductoData;
import com.playlandpark.coreservice.client.catalogo.dto.PromocionData;
import com.playlandpark.coreservice.integration.auth.AuthConsultaService;
import com.playlandpark.coreservice.integration.catalogo.CatalogoConsultaService;
import com.playlandpark.coreservice.ventas.dto.carrito.CarritoDescuentoRequest;
import com.playlandpark.coreservice.ventas.dto.carrito.CarritoItemRequest;
import com.playlandpark.coreservice.ventas.dto.carrito.CarritoRequest;
import com.playlandpark.coreservice.ventas.dto.carrito.CarritoResponse;
import com.playlandpark.coreservice.ventas.dto.carritodetalle.CarritoDetalleResponse;
import com.playlandpark.coreservice.ventas.entity.Carrito;
import com.playlandpark.coreservice.ventas.entity.CarritoDetalle;
import com.playlandpark.coreservice.ventas.repository.CarritoDetalleRepository;
import com.playlandpark.coreservice.ventas.repository.CarritoRepository;
import com.playlandpark.coreservice.ventas.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {

    private static final String ESTADO_ABIERTO = "ABIERTO";
    private static final String ROL_CLIENTE = "CLIENTE";
    private static final String ROL_EMPLEADO = "EMPLEADO";
    private static final Set<String> TIPOS_COMPRA_VALIDOS = Set.of("ONLINE", "LOCAL");

    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository carritoDetalleRepository;

    private final AuthConsultaService authConsultaService;
    private final CatalogoConsultaService catalogoConsultaService;

    @Override
    @Transactional
    public CarritoResponse getOrCreateActiveCart(CarritoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }

        if (request.idUsuario() == null) {
            throw new IllegalArgumentException("El idUsuario es obligatorio.");
        }

        if (request.tipoCompra() == null || request.tipoCompra().isBlank()) {
            throw new IllegalArgumentException("El tipoCompra es obligatorio.");
        }

        String tipoCompra = request.tipoCompra().trim().toUpperCase();

        if (!TIPOS_COMPRA_VALIDOS.contains(tipoCompra)) {
            throw new IllegalArgumentException("Tipo de compra inválido: " + request.tipoCompra());
        }

        UsuarioData usuario = authConsultaService.obtenerUsuarioParaCarrito(request.idUsuario());
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado: " + request.idUsuario());
        }

        validarRolPuedeCrearCarrito(usuario);
        validarTipoCompraPorRol(usuario, tipoCompra);

        Carrito cart = carritoRepository.findFirstByIdUsuarioAndEstado(request.idUsuario(), ESTADO_ABIERTO)
                .orElseGet(() -> {
                    Carrito c = new Carrito();
                    c.setIdUsuario(request.idUsuario());
                    c.setEstado(ESTADO_ABIERTO);
                    c.setTipoCompra(tipoCompra);
                    c.setFechaCreacion(LocalDateTime.now());
                    return carritoRepository.save(c);
                });

        if (cart.getTipoCompra() == null || cart.getTipoCompra().isBlank()) {
            cart.setTipoCompra(tipoCompra);
            carritoRepository.save(cart);
        }

        validarTipoCompra(cart);
        validarTipoCompraPorRol(usuario, cart.getTipoCompra());

        return recalculate(cart.getIdCarrito());
    }

    @Override
    @Transactional(readOnly = true)
    public CarritoResponse findById(Integer idCarrito) {
        Carrito cart = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado: " + idCarrito));
        return mapToResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarritoResponse> findAllActiveCart() {
        List<Carrito> carts = carritoRepository.findByEstado(ESTADO_ABIERTO);
        return carts.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public CarritoResponse addItem(CarritoItemRequest request) {
        validateItemRequest(request);

        Carrito cart = getOpenCart(request.idCarrito());
        validarTipoCompra(cart);

        ProductoData product = catalogoConsultaService.obtenerProducto(request.idProducto());
        validarProductoCarrito(product);
        validarStockSiAplica(product, request.cantidad());

        CarritoDetalle detail = carritoDetalleRepository
                .findByCarrito_IdCarritoAndIdProducto(cart.getIdCarrito(), product.idProducto())
                .orElse(null);

        if (detail == null) {
            detail = new CarritoDetalle();
            detail.setCarrito(cart);
            detail.setIdProducto(product.idProducto());
            detail.setDescripcionProducto(product.descripcion());
            detail.setPrecio(product.precio());
            detail.setCantidad(request.cantidad());
            detail.setDescuento(BigDecimal.ZERO);
            detail.setIdPromocion(null);

            BigDecimal base = calcularBase(detail.getPrecio(), detail.getCantidad());
            detail.setSubtotal(base);

            carritoDetalleRepository.save(detail);
        } else {
            validarStockSiAplica(product, request.cantidad());

            detail.setDescripcionProducto(product.descripcion());
            detail.setPrecio(product.precio());
            detail.setCantidad(request.cantidad());

            BigDecimal base = calcularBase(detail.getPrecio(), detail.getCantidad());
            detail.setDescuento(BigDecimal.ZERO);
            detail.setSubtotal(base);
            detail.setIdPromocion(null);

            carritoDetalleRepository.save(detail);
        }

        return recalculate(cart.getIdCarrito());
    }

    @Override
    @Transactional
    public CarritoResponse updateItemQuantity(CarritoItemRequest request) {
        validateItemRequest(request);

        Carrito cart = getOpenCart(request.idCarrito());

        CarritoDetalle detail = carritoDetalleRepository
                .findByCarrito_IdCarritoAndIdProducto(cart.getIdCarrito(), request.idProducto())
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe en el carrito."));

        ProductoData product = catalogoConsultaService.obtenerProducto(request.idProducto());
        validarProductoCarrito(product);
        validarStockSiAplica(product, request.cantidad());

        detail.setDescripcionProducto(product.descripcion());
        detail.setPrecio(product.precio());
        detail.setCantidad(request.cantidad());

        BigDecimal base = calcularBase(detail.getPrecio(), detail.getCantidad());
        detail.setDescuento(BigDecimal.ZERO);
        detail.setSubtotal(base);
        detail.setIdPromocion(null);

        carritoDetalleRepository.save(detail);

        return recalculate(cart.getIdCarrito());
    }

    @Override
    @Transactional
    public CarritoResponse removeItem(Integer idCarrito, Integer idProducto) {
        if (idCarrito == null) {
            throw new IllegalArgumentException("El idCarrito es obligatorio.");
        }
        if (idProducto == null) {
            throw new IllegalArgumentException("El idProducto es obligatorio.");
        }

        Carrito cart = getOpenCart(idCarrito);

        CarritoDetalle detail = carritoDetalleRepository
                .findByCarrito_IdCarritoAndIdProducto(cart.getIdCarrito(), idProducto)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe en el carrito."));

        carritoDetalleRepository.delete(detail);

        return recalculate(cart.getIdCarrito());
    }

    @Override
    @Transactional
    public CarritoResponse applyPromotion(Integer idCarrito, CarritoDescuentoRequest request) {
        if (idCarrito == null) {
            throw new IllegalArgumentException("El idCarrito es obligatorio.");
        }
        if (request == null || request.codigoPromocion() == null || request.codigoPromocion().isBlank()) {
            throw new IllegalArgumentException("El código de promoción es obligatorio.");
        }

        Carrito cart = getOpenCart(idCarrito);

        if (isPromotionExpiredByCartAge(cart)) {
            clearPromotion(cart);
            throw new IllegalArgumentException("El carrito tiene más de 3 días. No se puede aplicar el código.");
        }

        PromocionData promo = catalogoConsultaService.obtenerPromocionPorCodigo(request.codigoPromocion());
        validatePromotionVigency(promo);

        cart.setCodigoPromocion(promo.codigo());
        cart.setIdPromocion(promo.idPromocion());
        carritoRepository.save(cart);

        return recalculate(cart.getIdCarrito());
    }

    @Override
    @Transactional
    public CarritoResponse removePromotion(Integer idCarrito) {
        Carrito cart = getOpenCart(idCarrito);
        clearPromotion(cart);
        return recalculate(cart.getIdCarrito());
    }

    @Override
    @Transactional
    public CarritoResponse recalculate(Integer idCarrito) {
        Carrito cart = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado: " + idCarrito));

        if (!ESTADO_ABIERTO.equalsIgnoreCase(cart.getEstado())) {
            return mapToResponse(cart);
        }

        if (isPromotionExpiredByCartAge(cart)) {
            clearPromotion(cart);
        }

        PromocionData promo = null;
        if (cart.getCodigoPromocion() != null && !cart.getCodigoPromocion().isBlank()) {
            try {
                promo = catalogoConsultaService.obtenerPromocionPorCodigo(cart.getCodigoPromocion());

                if (!isPromotionVigent(promo)) {
                    clearPromotion(cart);
                    promo = null;
                }
            } catch (Exception e) {
                clearPromotion(cart);
                promo = null;
            }
        }

        List<CarritoDetalle> details = carritoDetalleRepository.findByCarrito_IdCarrito(cart.getIdCarrito());

        for (CarritoDetalle d : details) {
            BigDecimal base = calcularBase(d.getPrecio(), d.getCantidad());
            d.setDescuento(BigDecimal.ZERO);
            d.setSubtotal(base);
            d.setIdPromocion(null);
        }

        if (promo != null && promo.productosIds() != null && !promo.productosIds().isEmpty()) {
            BigDecimal remainingMax = promo.montoMax() != null ? promo.montoMax() : BigDecimal.ZERO;
            BigDecimal percent = promo.porcentaje() != null ? promo.porcentaje() : BigDecimal.ZERO;

            for (CarritoDetalle d : details) {
                if (remainingMax.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                if (!promo.productosIds().contains(d.getIdProducto())) {
                    continue;
                }

                BigDecimal base = calcularBase(d.getPrecio(), d.getCantidad());
                BigDecimal lineDiscount = base.multiply(percent);

                if (lineDiscount.compareTo(remainingMax) > 0) {
                    lineDiscount = remainingMax;
                }

                d.setDescuento(lineDiscount);
                d.setSubtotal(base.subtract(lineDiscount));
                d.setIdPromocion(promo.idPromocion());

                remainingMax = remainingMax.subtract(lineDiscount);
            }
        }

        carritoDetalleRepository.saveAll(details);

        return mapToResponse(cart);
    }

    private void validarRolPuedeCrearCarrito(UsuarioData usuario) {
        String rol = usuario.rolPrincipal();

        if (!ROL_CLIENTE.equalsIgnoreCase(rol) && !ROL_EMPLEADO.equalsIgnoreCase(rol)) {
            throw new IllegalArgumentException("El rol " + rol + " no puede crear carritos.");
        }
    }

    private void validarTipoCompra(Carrito cart) {
        if (cart.getTipoCompra() == null || cart.getTipoCompra().isBlank()) {
            throw new IllegalArgumentException("El tipoCompra del carrito es obligatorio.");
        }

        if (!TIPOS_COMPRA_VALIDOS.contains(cart.getTipoCompra().toUpperCase())) {
            throw new IllegalArgumentException("Tipo de compra inválido: " + cart.getTipoCompra());
        }
    }

    private void validarTipoCompraPorRol(UsuarioData usuario, String tipoCompra) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario inválido.");
        }

        if (tipoCompra == null || tipoCompra.isBlank()) {
            throw new IllegalArgumentException("El tipoCompra es obligatorio.");
        }

        String rol = usuario.rolPrincipal();
        String tipo = tipoCompra.trim().toUpperCase();

        if (ROL_CLIENTE.equalsIgnoreCase(rol) && !"ONLINE".equals(tipo)) {
            throw new IllegalArgumentException("El cliente solo puede usar tipoCompra ONLINE.");
        }

        if (ROL_EMPLEADO.equalsIgnoreCase(rol) && !"LOCAL".equals(tipo)) {
            throw new IllegalArgumentException("El empleado solo puede usar tipoCompra LOCAL.");
        }
    }

    private void validarProductoCarrito(ProductoData product) {
        if (product == null) {
            throw new IllegalArgumentException("Producto no encontrado.");
        }

        if (Boolean.FALSE.equals(product.activo())) {
            throw new IllegalArgumentException("El producto está inactivo: " + product.idProducto());
        }

        if (Boolean.TRUE.equals(product.esServicio())) {
            throw new IllegalArgumentException("Los servicios no se agregan al carrito.");
        }
    }

    private void validarStockSiAplica(ProductoData product, Integer cantidadSolicitada) {
        if (Boolean.FALSE.equals(product.esServicio())) {
            if (product.stockAct() == null || product.stockAct() < cantidadSolicitada) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para el producto: "
                                + product.idProducto() + " - " + product.descripcion()
                );
            }
        }
    }

    private Carrito getOpenCart(Integer idCarrito) {
        if (idCarrito == null) {
            throw new IllegalArgumentException("El idCarrito es obligatorio.");
        }

        Carrito cart = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado: " + idCarrito));

        if (!ESTADO_ABIERTO.equalsIgnoreCase(cart.getEstado())) {
            throw new IllegalArgumentException("El carrito no está en estado ABIERTO.");
        }

        return cart;
    }

    private void validateItemRequest(CarritoItemRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }
        if (request.idCarrito() == null) {
            throw new IllegalArgumentException("El idCarrito es obligatorio.");
        }
        if (request.idProducto() == null) {
            throw new IllegalArgumentException("El idProducto es obligatorio.");
        }
        if (request.cantidad() == null) {
            throw new IllegalArgumentException("La cantidad es obligatoria.");
        }
        if (request.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
    }

    private boolean isPromotionExpiredByCartAge(Carrito cart) {
        return cart.getFechaCreacion() != null &&
                cart.getFechaCreacion().plusDays(3).isBefore(LocalDateTime.now());
    }

    private void validatePromotionVigency(PromocionData promo) {
        if (promo == null || !isPromotionVigent(promo)) {
            throw new IllegalArgumentException("La promoción no está vigente o está inactiva.");
        }
    }

    private boolean isPromotionVigent(PromocionData promo) {
        if (promo.activo() == null || !promo.activo()) {
            return false;
        }

        LocalDate today = LocalDate.now();
        if (promo.fechaInicio() != null && today.isBefore(promo.fechaInicio())) {
            return false;
        }
        if (promo.fechaFin() != null && today.isAfter(promo.fechaFin())) {
            return false;
        }

        return true;
    }

    private void clearPromotion(Carrito cart) {
        cart.setCodigoPromocion(null);
        cart.setIdPromocion(null);
        carritoRepository.save(cart);
    }

    private BigDecimal calcularBase(BigDecimal precio, Integer cantidad) {
        BigDecimal precioSeguro = precio != null ? precio : BigDecimal.ZERO;
        int cantidadSegura = cantidad != null ? cantidad : 0;
        return precioSeguro.multiply(BigDecimal.valueOf(cantidadSegura));
    }

    private CarritoResponse mapToResponse(Carrito cart) {
        var details = carritoDetalleRepository.findByCarrito_IdCarrito(cart.getIdCarrito());

        var detailResponses = details.stream().map(d ->
                new CarritoDetalleResponse(
                        d.getIdCarritoDetalle(),
                        d.getIdProducto(),
                        d.getDescripcionProducto(),
                        d.getPrecio(),
                        d.getCantidad(),
                        d.getDescuento(),
                        d.getSubtotal()
                )
        ).toList();

        return new CarritoResponse(
                cart.getIdCarrito(),
                cart.getIdUsuario(),
                cart.getEstado(),
                cart.getTipoCompra(),
                cart.getCodigoPromocion(),
                cart.getFechaCreacion(),
                detailResponses
        );
    }
}