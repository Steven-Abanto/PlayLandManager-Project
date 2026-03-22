package com.playlandpark.coreservice.operacion.summary;

import com.playlandpark.coreservice.operacion.enums.EstadoJuego;

import java.time.LocalDate;

public record JuegoSummary(
        Integer idJuego,
        String codigo,
        String nombre,
        EstadoJuego estado,
        LocalDate proxMant,
        Boolean activo
) {}
