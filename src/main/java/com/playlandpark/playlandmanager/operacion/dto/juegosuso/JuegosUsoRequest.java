package com.playlandpark.playlandmanager.operacion.dto.juegosuso;

import java.time.LocalDate;

public record JuegosUsoRequest(
        Integer idJuego,
        String cantidadUso,
        LocalDate fechaUso,
        String descripcion
) {}