package com.playlandpark.coreservice.operacion.dto.juego;

import com.playlandpark.coreservice.operacion.enums.EstadoJuego;

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