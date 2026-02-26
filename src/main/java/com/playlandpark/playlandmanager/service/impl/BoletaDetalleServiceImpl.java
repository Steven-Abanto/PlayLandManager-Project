package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.boletadetalle.BoletaDetalleRequest;
import com.playlandpark.playlandmanager.model.dto.boletadetalle.BoletaDetalleResponse;
import com.playlandpark.playlandmanager.model.entity.Boleta;
import com.playlandpark.playlandmanager.model.entity.BoletaDetalle;
import com.playlandpark.playlandmanager.model.entity.Producto;
import com.playlandpark.playlandmanager.repository.BoletaDetalleRepository;
import com.playlandpark.playlandmanager.repository.BoletaRepository;
import com.playlandpark.playlandmanager.repository.ProductoRepository;
import com.playlandpark.playlandmanager.service.BoletaDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoletaDetalleServiceImpl implements BoletaDetalleService {

    private final BoletaDetalleRepository boletaDetalleRepository;
    private final BoletaRepository boletaRepository;
    private final ProductoRepository productoRepository;

    // Crea un detalle de boleta
    @Override
    @Transactional
    public BoletaDetalleResponse create(Integer idBoleta, BoletaDetalleRequest request) {
        validateRequest(idBoleta, request);

        Boleta boleta = boletaRepository.findById(idBoleta)
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada: " + idBoleta));

        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.idProducto()));

        BigDecimal precio = producto.getPrecio();
        BigDecimal descuento = request.descuento() == null ? BigDecimal.ZERO : request.descuento();
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(request.cantidad()))
                .subtract(descuento);

        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo.");
        }

        BoletaDetalle d = new BoletaDetalle();
        d.setBoleta(boleta);
        d.setProducto(producto);
        d.setPrecio(precio);
        d.setCantidad(request.cantidad());
        d.setDescuento(descuento);
        d.setSubtotal(subtotal);
        d.setPromocion(null); // se conectará cuando apliques promociones

        BoletaDetalle saved = boletaDetalleRepository.save(d);
        return mapToResponse(saved);
    }

    // Busca un detalle de boleta por su id.
    @Override
    @Transactional(readOnly = true)
    public BoletaDetalleResponse findById(Integer idBoletaDetalle) {
        BoletaDetalle d = boletaDetalleRepository.findById(idBoletaDetalle)
                .orElseThrow(() -> new IllegalArgumentException("BoletaDetalle no encontrado: " + idBoletaDetalle));
        return mapToResponse(d);
    }

    // Recupera todos los detalles de boleta
    @Override
    @Transactional(readOnly = true)
    public List<BoletaDetalleResponse> findAll() {
        return boletaDetalleRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // Recupera los detalles de boleta asociados a una boleta
    @Override
    @Transactional(readOnly = true)
    public List<BoletaDetalleResponse> findByBoleta(Integer idBoleta) {
        if (idBoleta == null) throw new IllegalArgumentException("El idBoleta es obligatorio.");
        return boletaDetalleRepository.findByBoleta_IdBoleta(idBoleta).stream().map(this::mapToResponse).toList();
    }

    // Recupera los detalles de boleta asociados a un producto
    @Override
    @Transactional(readOnly = true)
    public List<BoletaDetalleResponse> findByProducto(Integer idProducto) {
        if (idProducto == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        return boletaDetalleRepository.findByProducto_IdProducto(idProducto).stream().map(this::mapToResponse).toList();
    }

    // Recupera los detalles de boleta asociados a una promoción
    @Override
    @Transactional(readOnly = true)
    public List<BoletaDetalleResponse> findByPromocion(Integer idPromocion) {
        if (idPromocion == null) throw new IllegalArgumentException("El idPromocion es obligatorio.");
        return boletaDetalleRepository.findByPromocion_IdPromocion(idPromocion).stream().map(this::mapToResponse).toList();
    }

    // Actualiza un detalle de boleta
    @Override
    @Transactional
    public BoletaDetalleResponse update(Integer idBoletaDetalle, BoletaDetalleRequest request) {
        if (idBoletaDetalle == null) throw new IllegalArgumentException("El idBoletaDetalle es obligatorio.");
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idProducto() == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        if (request.cantidad() == null || request.cantidad() <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");

        BoletaDetalle d = boletaDetalleRepository.findById(idBoletaDetalle)
                .orElseThrow(() -> new IllegalArgumentException("BoletaDetalle no encontrado: " + idBoletaDetalle));

        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.idProducto()));

        BigDecimal precio = producto.getPrecio();
        BigDecimal descuento = request.descuento() == null ? BigDecimal.ZERO : request.descuento();
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(request.cantidad()))
                .subtract(descuento);

        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo.");
        }

        d.setProducto(producto);
        d.setPrecio(precio);
        d.setCantidad(request.cantidad());
        d.setDescuento(descuento);
        d.setSubtotal(subtotal);

        BoletaDetalle saved = boletaDetalleRepository.save(d);
        return mapToResponse(saved);
    }

    // Elimina un detalle de boleta por su identificador
    @Override
    @Transactional
    public void delete(Integer idBoletaDetalle) {
        if (idBoletaDetalle == null) throw new IllegalArgumentException("El idBoletaDetalle es obligatorio.");
        if (!boletaDetalleRepository.existsById(idBoletaDetalle)) {
            throw new IllegalArgumentException("BoletaDetalle no encontrado: " + idBoletaDetalle);
        }
        boletaDetalleRepository.deleteById(idBoletaDetalle);
    }

    // ---------------- helpers ----------------

    // Valida los datos para crear un detalle de boleta
    private void validateRequest(Integer idBoleta, BoletaDetalleRequest request) {
        if (idBoleta == null) throw new IllegalArgumentException("El idBoleta es obligatorio.");
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idProducto() == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        if (request.cantidad() == null || request.cantidad() <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        if (request.descuento() != null && request.descuento().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo.");
        }
    }

    // Convierte la entidad BoletaDetalle a su DTO de respuesta
    private BoletaDetalleResponse mapToResponse(BoletaDetalle d) {
        return new BoletaDetalleResponse(
                d.getIdBoletaDetalle(),
                d.getProducto().getIdProducto(),
                d.getProducto().getDescripcion(),
                d.getPrecio(),
                d.getCantidad(),
                d.getDescuento(),
                d.getSubtotal()
        );
    }
}