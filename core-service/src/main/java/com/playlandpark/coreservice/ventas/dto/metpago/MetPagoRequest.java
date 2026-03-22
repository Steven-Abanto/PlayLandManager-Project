package com.playlandpark.coreservice.ventas.dto.metpago;

import java.math.BigDecimal;

public record MetPagoRequest(
        String metodoPago,
        BigDecimal monto
) {}
