package com.playlandpark.coreservice.ventas.dto.carritodetalle;

import java.math.BigDecimal;

public record CarritoDetalleResponse(
        Integer idCarritoDetalle,
        Integer idProducto,
        String descripcion,
        BigDecimal precio,
        Integer cantidad,
        BigDecimal descuento,
        BigDecimal subtotal
) {}
