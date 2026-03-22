package com.playlandpark.coreservice.clients.catalogo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromocionData(
        Integer idPromocion,
        String codigo,
        BigDecimal porcentaje,
        BigDecimal montoMax,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean activo,
        List<Integer> productos
) {
}