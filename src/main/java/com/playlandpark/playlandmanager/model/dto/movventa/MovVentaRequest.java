package com.playlandpark.playlandmanager.model.dto.movventa;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovVentaRequest(
        Integer idCaja,
        BigDecimal monto,
        LocalDate fecha,
        String tipoMovimiento
) {}