package com.playlandpark.playlandmanager.model.dto.reservaservicio;

import com.playlandpark.playlandmanager.model.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaServicioResponse(
        Integer idReserva,
        Integer idProducto,
        String descripcionProducto,
        Integer idCliente,
        String nombreCliente,
        LocalDate fechaReserva,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer cntPersonas,
        EstadoReserva estado,
        String observaciones
) {}
