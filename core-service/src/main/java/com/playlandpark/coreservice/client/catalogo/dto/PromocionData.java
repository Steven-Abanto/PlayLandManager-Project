package com.playlandpark.coreservice.client.catalogo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record PromocionData(
        Integer idPromocion,
        String codigo,
        BigDecimal porcentaje,
        BigDecimal montoMax,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean activo,
        Set<String> productos
) {
}