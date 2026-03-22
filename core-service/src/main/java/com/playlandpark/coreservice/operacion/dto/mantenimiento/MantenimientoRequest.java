package com.playlandpark.coreservice.operacion.dto.mantenimiento;

import java.time.LocalDate;

public record MantenimientoRequest(
        Integer idEmpleado,
        Integer idJuego,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String resultado,
        String observaciones
) {}