package com.playlandpark.coreservice.operacion.dto.juegosuso;

import java.time.LocalDate;

public record JuegosUsoRequest(
        Integer idJuego,
        String cantidadUso,
        LocalDate fechaUso,
        String descripcion
) {}