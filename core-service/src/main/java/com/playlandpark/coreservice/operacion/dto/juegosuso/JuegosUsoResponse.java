package com.playlandpark.coreservice.operacion.dto.juegosuso;

import com.playlandpark.coreservice.operacion.summary.JuegoSummary;

import java.time.LocalDate;

public record JuegosUsoResponse(
        Integer idJuegosUso,
        JuegoSummary juego,
        String cantidadUso,
        LocalDate fechaUso,
        String descripcion
) {}