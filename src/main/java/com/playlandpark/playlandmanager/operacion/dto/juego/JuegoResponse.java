package com.playlandpark.playlandmanager.operacion.dto.juego;

import com.playlandpark.playlandmanager.operacion.enums.EstadoJuego;

import java.time.LocalDate;

public record JuegoResponse(
        Integer idJuego,
        String codigo,
        String nombre,
        String tipo,
        String descripcion,
        EstadoJuego estado,
        LocalDate ultMant,
        LocalDate proxMant,
        Boolean activo
) {}