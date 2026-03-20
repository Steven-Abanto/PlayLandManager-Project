package com.playlandpark.playlandmanager.ventas.dto.metpago;

import java.math.BigDecimal;

public record MetPagoRequest(
        String metodoPago,
        BigDecimal monto
) {}
