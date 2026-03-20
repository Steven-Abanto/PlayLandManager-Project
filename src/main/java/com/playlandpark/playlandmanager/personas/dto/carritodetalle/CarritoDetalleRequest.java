package com.playlandpark.playlandmanager.personas.dto.carritodetalle;

import java.math.BigDecimal;

public record CarritoDetalleRequest(
        Integer idCarrito,
        Integer idProducto,
        BigDecimal precio,
        Integer cantidad,
        BigDecimal descuento
) {}
