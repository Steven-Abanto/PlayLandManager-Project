package com.playlandpark.catalogoservice.catalogo.dto.promocion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record PromocionResponse(
        Integer idPromocion,
        String codigo,
        String nombre,
        String descripcion,
        String imagenUrl,
        BigDecimal porcentaje,
        BigDecimal montoMax,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean activo,
        Set<Integer> productosIds,
        Set<String> productos
) {}