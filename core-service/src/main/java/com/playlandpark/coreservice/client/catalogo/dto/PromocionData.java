package com.playlandpark.coreservice.client.catalogo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record PromocionData(
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