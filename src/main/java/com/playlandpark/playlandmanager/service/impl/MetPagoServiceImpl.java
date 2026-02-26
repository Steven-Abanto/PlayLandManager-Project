package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.metpago.MetPagoRequest;
import com.playlandpark.playlandmanager.model.dto.metpago.MetPagoResponse;
import com.playlandpark.playlandmanager.model.entity.Boleta;
import com.playlandpark.playlandmanager.model.entity.MetPago;
import com.playlandpark.playlandmanager.repository.BoletaRepository;
import com.playlandpark.playlandmanager.repository.MetPagoRepository;
import com.playlandpark.playlandmanager.service.MetPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetPagoServiceImpl implements MetPagoService {

    private static final String ESTADO_ANULADA = "ANULADA";

    private final MetPagoRepository metPagoRepository;
    private final BoletaRepository boletaRepository;

    // Crea un nuevo metodo de pago asociado a una boleta.
    @Override
    @Transactional
    public MetPagoResponse create(Integer idBoleta, MetPagoRequest request) {
        if (idBoleta == null) throw new IllegalArgumentException("El idBoleta es obligatorio.");
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");
        if (request.metodoPago() == null || request.metodoPago().isBlank()) {
            throw new IllegalArgumentException("El metodoPago es obligatorio.");
        }
        if (request.monto() == null) throw new IllegalArgumentException("El monto es obligatorio.");
        if (request.monto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que 0.");
        }

        Boleta boleta = boletaRepository.findById(idBoleta)
                .orElseThrow(() -> new IllegalArgumentException("Boleta no encontrada: " + idBoleta));

        if (ESTADO_ANULADA.equalsIgnoreCase(boleta.getEstado())) {
            throw new IllegalArgumentException("No se puede registrar pagos en una boleta ANULADA.");
        }

        BigDecimal totalPagado = metPagoRepository.sumMontoByBoleta(idBoleta);
        BigDecimal totalBoleta = boleta.getTotal() == null ? BigDecimal.ZERO : boleta.getTotal();
        BigDecimal nuevoTotalPagado = totalPagado.add(request.monto());

        if (nuevoTotalPagado.compareTo(totalBoleta) > 0) {
            BigDecimal saldo = totalBoleta.subtract(totalPagado);
            throw new IllegalArgumentException(
                    "El pago excede el total de la boleta. Saldo pendiente: " + saldo
            );
        }

        MetPago p = new MetPago();
        p.setBoleta(boleta);
        p.setMetodoPago(request.metodoPago().trim());
        p.setMonto(request.monto());

        MetPago saved = metPagoRepository.save(p);
        return mapToResponse(saved);
    }

    // Busca un metodo de pago por su id
    @Override
    @Transactional(readOnly = true)
    public MetPagoResponse findById(Integer idMetPago) {
        MetPago p = metPagoRepository.findById(idMetPago)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado: " + idMetPago));
        return mapToResponse(p);
    }

    // Recupera todos los metodos de pago
    @Override
    @Transactional(readOnly = true)
    public List<MetPagoResponse> findAll() {
        return metPagoRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // Recupera los metodos de pago asociados a una boleta
    @Override
    @Transactional(readOnly = true)
    public List<MetPagoResponse> findByBoleta(Integer idBoleta) {
        if (idBoleta == null) throw new IllegalArgumentException("El idBoleta es obligatorio.");
        return metPagoRepository.findByBoleta_IdBoleta(idBoleta).stream().map(this::mapToResponse).toList();
    }

    // Elimina un metodo de pago por su id
    @Override
    @Transactional
    public void delete(Integer idMetPago) {
        if (idMetPago == null) throw new IllegalArgumentException("El idMetPago es obligatorio.");
        if (!metPagoRepository.existsById(idMetPago)) {
            throw new IllegalArgumentException("Pago no encontrado: " + idMetPago);
        }
        metPagoRepository.deleteById(idMetPago);
    }

    // Mapea una entidad MetPago a un DTO MetPagoResponse
    private MetPagoResponse mapToResponse(MetPago p) {
        return new MetPagoResponse(
                p.getIdMetPago(),
                p.getMetodoPago(),
                p.getMonto()
        );
    }
}