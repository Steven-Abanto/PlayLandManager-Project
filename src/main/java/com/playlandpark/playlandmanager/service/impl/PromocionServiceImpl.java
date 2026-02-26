package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.promocion.PromocionRequest;
import com.playlandpark.playlandmanager.model.dto.promocion.PromocionResponse;
import com.playlandpark.playlandmanager.model.dto.summary.PromocionSummary;
import com.playlandpark.playlandmanager.model.entity.Producto;
import com.playlandpark.playlandmanager.model.entity.Promocion;
import com.playlandpark.playlandmanager.repository.ProductoRepository;
import com.playlandpark.playlandmanager.repository.PromocionRepository;
import com.playlandpark.playlandmanager.service.PromocionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PromocionServiceImpl implements PromocionService {

    private final PromocionRepository promocionRepository;
    private final ProductoRepository productoRepository;

    // Crea una nueva promoción a partir de un request
    @Override
    public PromocionResponse create(PromocionRequest request) {
        validateRequest(request);

        if (promocionRepository.existsByCodigo(request.codigo())) {
            throw new IllegalArgumentException("El código de promoción ya existe: " + request.codigo());
        }

        Promocion promocion = new Promocion();
        applyRequest(promocion, request);

        Promocion saved = promocionRepository.save(promocion);
        return mapToResponse(saved);
    }

    // Busca una promoción por su id
    @Override
    @Transactional(readOnly = true)
    public PromocionResponse findById(Integer idPromocion) {
        return mapToResponse(getPromocion(idPromocion));
    }

    // Busca una promoción por su código
    @Override
    @Transactional(readOnly = true)
    public PromocionResponse findByCodigo(String codigo) {
        Promocion promocion = promocionRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada con código: " + codigo));
        return mapToResponse(promocion);
    }

    // Busca todas las promociones, con opción de filtrar solo las activas
    @Override
    @Transactional(readOnly = true)
    public List<PromocionResponse> findAll(boolean onlyActive) {
        List<Promocion> list = onlyActive
                ? promocionRepository.findByActivoTrue()
                : promocionRepository.findAll();

        return list.stream().map(this::mapToResponse).toList();
    }

    // Busca todas las promociones en formato resumen, con opción de filtrar solo las activas
    @Override
    @Transactional(readOnly = true)
    public List<PromocionSummary> findAllSummary(boolean onlyActive) {
        List<Promocion> list = onlyActive
                ? promocionRepository.findByActivoTrue()
                : promocionRepository.findAll();

        return list.stream().map(this::mapToSummary).toList();
    }

    // Busca todas las promociones activas en el día de hoy
    @Override
    @Transactional(readOnly = true)
    public List<PromocionResponse> findActiveToday() {
        LocalDate today = LocalDate.now();

        List<Promocion> list =
                promocionRepository
                        .findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqualAndActivoTrue(today, today);

        return list.stream().map(this::mapToResponse).toList();
    }

    // Actualiza una promoción existente
    @Override
    public PromocionResponse update(Integer idPromocion, PromocionRequest request) {

        Promocion promocion = getPromocion(idPromocion);

        if (request.codigo() != null &&
                !request.codigo().equals(promocion.getCodigo()) &&
                promocionRepository.existsByCodigo(request.codigo())) {
            throw new IllegalArgumentException("El código de promoción ya existe: " + request.codigo());
        }

        applyRequest(promocion, request);

        return mapToResponse(promocionRepository.save(promocion));
    }

    // Elimina lógicamente una promoción (activo = false)
    @Override
    public void logicDelete(Integer idPromocion) {
        Promocion promocion = getPromocion(idPromocion);
        promocion.setActivo(false);
        promocionRepository.save(promocion);
    }

    // Agrega un producto a la promoción
    @Override
    public PromocionResponse addProducto(Integer idPromocion, Integer idProducto) {

        Promocion promocion = getPromocion(idPromocion);
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + idProducto));

        promocion.getProductos().add(producto);

        return mapToResponse(promocionRepository.save(promocion));
    }

    // Elimina un producto de la promoción
    @Override
    public PromocionResponse removeProducto(Integer idPromocion, Integer idProducto) {

        Promocion promocion = getPromocion(idPromocion);

        promocion.getProductos()
                .removeIf(p -> p.getIdProducto().equals(idProducto));

        return mapToResponse(promocionRepository.save(promocion));
    }

    // -------- Helpers --------
    // Obtiene una promoción por id
    private Promocion getPromocion(Integer id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada con id: " + id));
    }

    // Valida los datos de un request de promoción, tanto para creación como para actualización
    private void validateRequest(PromocionRequest request) {
        if (request.codigo() == null || request.codigo().isBlank())
            throw new IllegalArgumentException("El código es obligatorio.");

        if (request.porcentaje() == null ||
                request.porcentaje().compareTo(BigDecimal.ZERO) <= 0 ||
                request.porcentaje().compareTo(BigDecimal.ONE) > 0)
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 1.");

        if (request.montoMax() == null ||
                request.montoMax().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El monto máximo no puede ser negativo.");

        if (request.fechaFin().isBefore(request.fechaInicio()))
            throw new IllegalArgumentException("La fecha fin no puede ser menor que la fecha inicio.");
    }

    // Aplica los datos de un request a una entidad de promoción
    private void applyRequest(Promocion promocion, PromocionRequest request) {

        if (request.codigo() != null) promocion.setCodigo(request.codigo());
        if (request.nombre() != null) promocion.setNombre(request.nombre());
        if (request.descripcion() != null) promocion.setDescripcion(request.descripcion());
        if (request.porcentaje() != null) promocion.setPorcentaje(request.porcentaje());
        if (request.montoMax() != null) promocion.setMontoMax(request.montoMax());
        if (request.fechaInicio() != null) promocion.setFechaInicio(request.fechaInicio());
        if (request.fechaFin() != null) promocion.setFechaFin(request.fechaFin());
        if (request.activo() != null) promocion.setActivo(request.activo());

        if (request.productosIds() != null) {
            Set<Producto> productos = new HashSet<>();
            for (Integer id : request.productosIds()) {
                Producto p = productoRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
                productos.add(p);
            }
            promocion.setProductos(productos);
        }
    }

    // Mapea una entidad de promoción a su DTO de respuesta
    private PromocionResponse mapToResponse(Promocion p) {
        Set<Integer> productosIds = p.getProductos()
                .stream()
                .map(Producto::getIdProducto)
                .collect(java.util.stream.Collectors.toSet());

        // Buscar las descripciones de los productos
        Set<String> productos = productosIds
                .stream()
                .map(id -> {
                    // Para cada ID de producto, buscamos la descripción en el repositorio
                    Producto producto = productoRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
                    return producto.getDescripcion();  // Devolvemos la descripción
                })
                .collect(java.util.stream.Collectors.toSet());

        return new PromocionResponse(
                p.getIdPromocion(),
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPorcentaje(),
                p.getMontoMax(),
                p.getFechaInicio(),
                p.getFechaFin(),
                p.getActivo(),
                productos
        );
    }

    // Mapea una entidad de promoción a su DTO de resumen
    private PromocionSummary mapToSummary(Promocion p) {
        return new PromocionSummary(
                p.getIdPromocion(),
                p.getCodigo(),
                p.getNombre(),
                p.getPorcentaje(),
                p.getFechaInicio(),
                p.getFechaFin(),
                p.getActivo()
        );
    }
}
