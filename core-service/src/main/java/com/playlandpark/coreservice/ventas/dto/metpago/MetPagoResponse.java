package com.playlandpark.coreservice.ventas.dto.metpago;

import java.math.BigDecimal;

public record MetPagoResponse(
        Integer idMetPago,
        String metodoPago,
        BigDecimal monto
) {}
