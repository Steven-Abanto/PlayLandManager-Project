package com.playlandpark.coreservice.ventas.service.impl;

import com.playlandpark.coreservice.clients.catalogo.service.CatalogoClient;
import com.playlandpark.coreservice.clients.catalogo.dto.ProductoData;
import com.playlandpark.coreservice.ventas.dto.boletadetalle.BoletaDetalleRequest;
import com.playlandpark.coreservice.ventas.dto.boletadetalle.BoletaDetalleResponse;
import com.playlandpark.coreservice.ventas.entity.Boleta;
import com.playlandpark.coreservice.ventas.entity.BoletaDetalle;
import com.playlandpark.coreservice.ventas.repository.BoletaDetalleRepository;
import com.playlandpark.coreservice.ventas.repository.BoletaRepository;
import com.playlandpark.coreservice.ventas.service.BoletaDetalleService;
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
    private final CatalogoClient catalogoClient;

    @Override
    @Transactional
    public BoletaDetalleResponse create(Integer idBoleta, BoletaDetalleRequest request) {
        validateRequest(idBoleta, request);

        Boleta boleta = boletaRepository.findById(idBoleta)
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada: " + idBoleta));

        ProductoData producto = catalogoClient.obtenerProducto(request.idProducto());
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado: " + request.idProducto());
        }

        BigDecimal precio = producto.precio();
        BigDecimal descuento = request.descuento() == null ? BigDecimal.ZERO : request.descuento();
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(request.cantidad()))
                .subtract(descuento);

        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo.");
        }

        BoletaDetalle d = new BoletaDetalle();
        d.setBoleta(boleta);
        d.setIdProducto(producto.idProducto());
        d.setDescripcionProducto(producto.descripcion());
        d.setPrecio(precio);
        d.setCantidad(request.cantidad());
        d.setDescuento(descuento);
        d.setSubtotal(subtotal);
        d.setIdPromocion(null);

        BoletaDetalle saved = boletaDetalleRepository.save(d);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BoletaDetalleResponse findById(Integer idBoletaDetalle) {
        BoletaDetalle d = boletaDetalleRepository.findById(idBoletaDetalle)
                .orElseThrow(() -> new IllegalArgumentException("BoletaDetalle no encontrado: " + idBoletaDetalle));
        return mapToResponse(d);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoletaDetalleResponse> findAll() {
        return boletaDetalleRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoletaDetalleResponse> findByBoleta(Integer idBoleta) {
        if (idBoleta == null) throw new IllegalArgumentException("El idBoleta es obligatorio.");

        return boletaDetalleRepository.findByBoleta_IdBoleta(idBoleta).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoletaDetalleResponse> findByProducto(Integer idProducto) {
        if (idProducto == null) throw new IllegalArgumentException("El idProducto es obligatorio.");

        return boletaDetalleRepository.findByIdProducto(idProducto).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoletaDetalleResponse> findByPromocion(Integer idPromocion) {
        if (idPromocion == null) throw new IllegalArgumentException("El idPromocion es obligatorio.");

        return boletaDetalleRepository.findByIdPromocion(idPromocion).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public BoletaDetalleResponse update(Integer idBoletaDetalle, BoletaDetalleRequest request) {
        if (idBoletaDetalle == null) throw new IllegalArgumentException("El idBoletaDetalle es obligatorio.");
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idProducto() == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        if (request.cantidad() == null || request.cantidad() <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");

        BoletaDetalle d = boletaDetalleRepository.findById(idBoletaDetalle)
                .orElseThrow(() -> new IllegalArgumentException("BoletaDetalle no encontrado: " + idBoletaDetalle));

        ProductoData producto = catalogoClient.obtenerProducto(request.idProducto());
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado: " + request.idProducto());
        }

        BigDecimal precio = producto.precio();
        BigDecimal descuento = request.descuento() == null ? BigDecimal.ZERO : request.descuento();
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(request.cantidad()))
                .subtract(descuento);

        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo.");
        }

        d.setIdProducto(producto.idProducto());
        d.setDescripcionProducto(producto.descripcion());
        d.setPrecio(precio);
        d.setCantidad(request.cantidad());
        d.setDescuento(descuento);
        d.setSubtotal(subtotal);

        BoletaDetalle saved = boletaDetalleRepository.save(d);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Integer idBoletaDetalle) {
        if (idBoletaDetalle == null) throw new IllegalArgumentException("El idBoletaDetalle es obligatorio.");

        if (!boletaDetalleRepository.existsById(idBoletaDetalle)) {
            throw new IllegalArgumentException("BoletaDetalle no encontrado: " + idBoletaDetalle);
        }
        boletaDetalleRepository.deleteById(idBoletaDetalle);
    }

    private void validateRequest(Integer idBoleta, BoletaDetalleRequest request) {
        if (idBoleta == null) throw new IllegalArgumentException("El idBoleta es obligatorio.");
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idProducto() == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        if (request.cantidad() == null || request.cantidad() <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        if (request.descuento() != null && request.descuento().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo.");
        }
    }

    private BoletaDetalleResponse mapToResponse(BoletaDetalle d) {
        return new BoletaDetalleResponse(
                d.getIdBoletaDetalle(),
                d.getIdProducto(),
                d.getDescripcionProducto(),
                d.getPrecio(),
                d.getCantidad(),
                d.getDescuento(),
                d.getSubtotal()
        );
    }
}