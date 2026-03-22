package com.playlandpark.coreservice.operacion.dto.reservaservicio;

import com.playlandpark.coreservice.operacion.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaServicioResponse(
        Integer idReserva,
        Integer idProducto,
        Integer idCliente,
        String nombreCliente,
        LocalDate fechaReserva,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer cntPersonas,
        EstadoReserva estado,
        String observaciones
) {}
