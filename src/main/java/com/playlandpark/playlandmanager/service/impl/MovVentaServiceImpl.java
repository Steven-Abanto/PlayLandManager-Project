package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.movventa.MovVentaRequest;
import com.playlandpark.playlandmanager.model.dto.movventa.MovVentaResponse;
import com.playlandpark.playlandmanager.model.dto.summary.CajaSummary;
import com.playlandpark.playlandmanager.model.entity.Caja;
import com.playlandpark.playlandmanager.model.entity.MovVenta;
import com.playlandpark.playlandmanager.repository.CajaRepository;
import com.playlandpark.playlandmanager.repository.MovVentaRepository;
import com.playlandpark.playlandmanager.service.MovVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovVentaServiceImpl implements MovVentaService {

    private final MovVentaRepository movVentaRepository;
    private final CajaRepository cajaRepository;

    // Crea un nuevo movimiento de venta a partir de un request
    @Override
    @Transactional
    public MovVentaResponse create(MovVentaRequest request) {
        validate(request);

        Caja caja = cajaRepository.findById(request.idCaja())
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada: " + request.idCaja()));

        MovVenta mv = new MovVenta();
        mv.setCaja(caja);
        mv.setMonto(request.monto());
        mv.setFecha(request.fecha() != null ? request.fecha() : LocalDate.now());
        mv.setTipoMovimiento(request.tipoMovimiento().trim());

        MovVenta saved = movVentaRepository.save(mv);
        return mapToResponse(saved);
    }

    // Busca un movimiento de venta por su id
    @Override
    @Transactional(readOnly = true)
    public MovVentaResponse findById(Integer idMovVenta) {
        MovVenta mv = movVentaRepository.findById(idMovVenta)
                .orElseThrow(() -> new IllegalArgumentException("MovVenta no encontrado: " + idMovVenta));
        return mapToResponse(mv);
    }

    // Busca todos los movimientos de venta
    @Override
    @Transactional(readOnly = true)
    public List<MovVentaResponse> findAll() {
        return movVentaRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // Busca movimientos de venta por caja
    @Override
    @Transactional(readOnly = true)
    public List<MovVentaResponse> findByCaja(Integer idCaja) {
        if (idCaja == null) throw new IllegalArgumentException("El idCaja es obligatorio.");
        return movVentaRepository.findByCaja_IdCaja(idCaja).stream().map(this::mapToResponse).toList();
    }

    // Busca movimientos de venta por fecha
    @Override
    @Transactional(readOnly = true)
    public List<MovVentaResponse> findByFecha(LocalDate fecha) {
        if (fecha == null) throw new IllegalArgumentException("La fecha es obligatoria.");
        return movVentaRepository.findByFecha(fecha).stream().map(this::mapToResponse).toList();
    }

    // Busca movimientos de venta entre un rango de fechas
    @Override
    @Transactional(readOnly = true)
    public List<MovVentaResponse> findByRangoFechas(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("Las fechas son obligatorias.");
        if (end.isBefore(start)) throw new IllegalArgumentException("La fecha fin no puede ser menor que la fecha inicio.");
        return movVentaRepository.findByFechaBetween(start, end).stream().map(this::mapToResponse).toList();
    }

    // Busca movimientos de venta por tipo de movimiento
    @Override
    @Transactional(readOnly = true)
    public List<MovVentaResponse> findByTipoMovimiento(String tipoMovimiento) {
        if (tipoMovimiento == null || tipoMovimiento.isBlank()) {
            throw new IllegalArgumentException("El tipoMovimiento es obligatorio.");
        }
        return movVentaRepository.findByTipoMovimiento(tipoMovimiento.trim()).stream()
                .map(this::mapToResponse).toList();
    }

    // Elimina un movimiento de venta por su id
    @Override
    @Transactional
    public void delete(Integer idMovVenta) {
        if (idMovVenta == null) throw new IllegalArgumentException("El idMovVenta es obligatorio.");
        if (!movVentaRepository.existsById(idMovVenta)) {
            throw new IllegalArgumentException("MovVenta no encontrado: " + idMovVenta);
        }
        movVentaRepository.deleteById(idMovVenta);
    }

    // ---------------- helpers ----------------
    // Valida los datos del request para crear un movimiento de venta
    private void validate(MovVentaRequest request) {
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.idCaja() == null) throw new IllegalArgumentException("El idCaja es obligatorio.");
        if (request.monto() == null) throw new IllegalArgumentException("El monto es obligatorio.");
        if (request.monto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que 0.");
        }
        if (request.tipoMovimiento() == null || request.tipoMovimiento().isBlank()) {
            throw new IllegalArgumentException("El tipoMovimiento es obligatorio.");
        }
    }

    // Mapea un movimiento de venta a su respuesta DTO
    private MovVentaResponse mapToResponse(MovVenta mv) {
        Caja c = mv.getCaja();

        CajaSummary caja = new CajaSummary(
                c.getCodCaja()
        );

        return new MovVentaResponse(
                mv.getIdMovVenta(),
                caja,
                mv.getMonto(),
                mv.getFecha(),
                mv.getTipoMovimiento()
        );
    }
}