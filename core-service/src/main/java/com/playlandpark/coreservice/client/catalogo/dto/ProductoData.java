package com.playlandpark.coreservice.client.catalogo.dto;

import java.math.BigDecimal;

public record ProductoData(
        Integer idProducto,
        String descripcion,
        String categoria,
        String marca,
        BigDecimal precio,
        Integer stockMin,
        Integer stockAct,
        String upc,
        String sku,
        Boolean esServicio,
        Boolean activo
) {
}