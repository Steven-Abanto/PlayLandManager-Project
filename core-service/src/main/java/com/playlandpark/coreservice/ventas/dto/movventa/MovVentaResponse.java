package com.playlandpark.coreservice.ventas.dto.movventa;

import com.playlandpark.coreservice.ventas.summary.CajaSummary;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovVentaResponse(
        Integer idMovVenta,
        CajaSummary caja,
        BigDecimal monto,
        LocalDate fecha,
        String tipoMovimiento
) {}
