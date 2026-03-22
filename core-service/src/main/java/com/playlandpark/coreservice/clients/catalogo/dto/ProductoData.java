package com.playlandpark.coreservice.clients.catalogo.dto;

import java.math.BigDecimal;

public record ProductoData(
        Integer idProducto,
        String descripcion,
        BigDecimal precio,
        Boolean esServicio,
        Boolean activo,
        Integer stock
) {
}