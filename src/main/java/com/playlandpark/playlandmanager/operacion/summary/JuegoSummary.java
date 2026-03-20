package com.playlandpark.playlandmanager.operacion.summary;

import com.playlandpark.playlandmanager.operacion.enums.EstadoJuego;

import java.time.LocalDate;

public record JuegoSummary(
        Integer idJuego,
        String codigo,
        String nombre,
        EstadoJuego estado,
        LocalDate proxMant,
        Boolean activo
) {}
