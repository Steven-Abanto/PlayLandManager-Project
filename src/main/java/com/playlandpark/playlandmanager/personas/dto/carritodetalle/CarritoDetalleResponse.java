package com.playlandpark.playlandmanager.personas.dto.carritodetalle;

import com.playlandpark.playlandmanager.catalogo.summary.ProductoSummary;

import java.math.BigDecimal;

public record CarritoDetalleResponse(
        Integer idCarritoDetalle,
        ProductoSummary producto,
        BigDecimal precio,
        Integer cantidad,
        BigDecimal descuento,
        BigDecimal subtotal
) {}
