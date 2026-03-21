package com.playlandpark.catalogoservice.catalogo.summary;

import java.math.BigDecimal;

public record ProductoSummary(
        Integer idProducto,
        String descripcion,
        BigDecimal precio,
        Boolean esServicio,
        Boolean activo
) {}