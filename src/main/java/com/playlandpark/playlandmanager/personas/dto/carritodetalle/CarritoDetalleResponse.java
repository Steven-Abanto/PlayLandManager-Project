package com.playlandpark.playlandmanager.personas.dto.carritodetalle;

import com.playlandpark.playlandmanager.common.dto.summary.ProductoSummary;

import java.math.BigDecimal;

public record CarritoDetalleResponse(
        Integer idCarritoDetalle,
        ProductoSummary producto,
        BigDecimal precio,
        Integer cantidad,
        BigDecimal descuento,
        BigDecimal subtotal
) {}
