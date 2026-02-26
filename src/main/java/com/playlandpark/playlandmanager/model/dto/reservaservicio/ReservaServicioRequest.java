package com.playlandpark.playlandmanager.model.dto.reservaservicio;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaServicioRequest(
        Integer idProducto,
        Integer idCliente,
        LocalDate fechaReserva,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer cntPersonas,
        String observaciones
) {}