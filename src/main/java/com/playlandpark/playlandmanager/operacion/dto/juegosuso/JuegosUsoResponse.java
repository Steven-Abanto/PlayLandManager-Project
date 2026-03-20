package com.playlandpark.playlandmanager.operacion.dto.juegosuso;

import com.playlandpark.playlandmanager.operacion.summary.JuegoSummary;

import java.time.LocalDate;

public record JuegosUsoResponse(
        Integer idJuegosUso,
        JuegoSummary juego,
        String cantidadUso,
        LocalDate fechaUso,
        String descripcion
) {}