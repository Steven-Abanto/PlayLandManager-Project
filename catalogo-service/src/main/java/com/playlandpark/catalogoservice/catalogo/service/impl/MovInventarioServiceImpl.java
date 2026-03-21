package com.playlandpark.catalogoservice.catalogo.service.impl;

import com.playlandpark.catalogoservice.catalogo.dto.movinventario.MovInventarioProductoResponse;
import com.playlandpark.catalogoservice.catalogo.dto.movinventario.MovInventarioRequest;
import com.playlandpark.catalogoservice.catalogo.dto.movinventario.MovInventarioResponse;
import com.playlandpark.catalogoservice.catalogo.entity.MovInventario;
import com.playlandpark.catalogoservice.catalogo.entity.Producto;
import com.playlandpark.catalogoservice.catalogo.repository.MovInventarioRepository;
import com.playlandpark.catalogoservice.catalogo.repository.ProductoRepository;
import com.playlandpark.catalogoservice.catalogo.service.MovInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovInventarioServiceImpl implements MovInventarioService {

    private final MovInventarioRepository movInventarioRepository;
    private final ProductoRepository productoRepository;

    // Crea un nuevo movimiento de inventario a partir de un request
    @Override
    @Transactional
    public MovInventarioResponse create(MovInventarioRequest request) {
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idProducto() == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
        if (request.tipoMovimiento() == null || request.tipoMovimiento().isBlank()) {
            throw new IllegalArgumentException("El tipoMovimiento es obligatorio.");
        }

        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.idProducto()));

        MovInventario mi = new MovInventario();
        mi.setIdBoleta(request.idBoleta()); // solo referencia por ID
        mi.setProducto(producto);
        mi.setCantidad(request.cantidad());
        mi.setFecha(request.fecha() != null ? request.fecha() : LocalDate.now());
        mi.setTipoMovimiento(request.tipoMovimiento().trim());

        MovInventario saved = movInventarioRepository.save(mi);
        return mapToResponse(saved);
    }

    // Busca un movimiento de inventario por su id
    @Override
    @Transactional(readOnly = true)
    public MovInventarioResponse findById(Integer idMovInv) {
        MovInventario mi = movInventarioRepository.findById(idMovInv)
                .orElseThrow(() -> new IllegalArgumentException("MovInventario no encontrado: " + idMovInv));
        return mapToResponse(mi);
    }

    // Busca todos los movimientos de inventario
    @Override
    @Transactional(readOnly = true)
    public List<MovInventarioResponse> findAll() {
        return movInventarioRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // Busca movimientos de inventario por id de boleta
    @Override
    @Transactional(readOnly = true)
    public List<MovInventarioResponse> findByBoleta(Integer idBoleta) {
        if (idBoleta == null) throw new IllegalArgumentException("El idBoleta es obligatorio.");
        return movInventarioRepository.findByIdBoleta(idBoleta).stream().map(this::mapToResponse).toList();
    }

    // Busca movimientos de inventario por id de producto
    @Override
    @Transactional(readOnly = true)
    public List<MovInventarioResponse> findByProducto(Integer idProducto) {
        if (idProducto == null) throw new IllegalArgumentException("El idProducto es obligatorio.");
        return movInventarioRepository.findByProducto_IdProducto(idProducto).stream().map(this::mapToResponse).toList();
    }

    // Busca movimientos de inventario por fecha
    @Override
    @Transactional(readOnly = true)
    public List<MovInventarioResponse> findByFecha(LocalDate fecha) {
        if (fecha == null) throw new IllegalArgumentException("La fecha es obligatoria.");
        return movInventarioRepository.findByFecha(fecha).stream().map(this::mapToResponse).toList();
    }

    // Busca movimientos de inventario por rango de fechas
    @Override
    @Transactional(readOnly = true)
    public List<MovInventarioResponse> findByRangoFechas(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("Las fechas son obligatorias.");
        if (end.isBefore(start)) throw new IllegalArgumentException("La fecha fin no puede ser menor que la fecha inicio.");
        return movInventarioRepository.findByFechaBetween(start, end).stream().map(this::mapToResponse).toList();
    }

    // Busca movimientos de inventario por tipo de movimiento
    @Override
    @Transactional(readOnly = true)
    public List<MovInventarioResponse> findByTipoMovimiento(String tipoMovimiento) {
        if (tipoMovimiento == null || tipoMovimiento.isBlank()) {
            throw new IllegalArgumentException("El tipoMovimiento es obligatorio.");
        }
        return movInventarioRepository.findByTipoMovimiento(tipoMovimiento.trim()).stream()
                .map(this::mapToResponse).toList();
    }

    // Elimina un movimiento de inventario por su id
    @Override
    @Transactional
    public void delete(Integer idMovInv) {
        if (idMovInv == null) throw new IllegalArgumentException("El idMovInv es obligatorio.");
        if (!movInventarioRepository.existsById(idMovInv)) {
            throw new IllegalArgumentException("MovInventario no encontrado: " + idMovInv);
        }
        movInventarioRepository.deleteById(idMovInv);
    }

    // ---------------- helpers ----------------
    // Mapea una entidad MovInventario a un DTO MovInventarioResponse
    private MovInventarioResponse mapToResponse(MovInventario mi) {
        Producto p = mi.getProducto();
        MovInventarioProductoResponse producto = new MovInventarioProductoResponse(p.getIdProducto(),
                                                       p.getDescripcion());

        return new MovInventarioResponse(
                mi.getIdMovInv(),
                mi.getIdBoleta(),
                producto,
                mi.getCantidad(),
                mi.getFecha(),
                mi.getTipoMovimiento()
        );
    }
}