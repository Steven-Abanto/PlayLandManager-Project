package com.playlandpark.playlandmanager.catalogo.dto.producto;

import java.math.BigDecimal;

public record ProductoRequest(
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
) {}