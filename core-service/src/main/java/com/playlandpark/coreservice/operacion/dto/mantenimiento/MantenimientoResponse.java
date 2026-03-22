package com.playlandpark.coreservice.operacion.dto.mantenimiento;

import com.playlandpark.coreservice.operacion.summary.JuegoSummary;

import java.time.LocalDate;

public record MantenimientoResponse(
        Integer idMantenimiento,
        Integer idEmpleado,
        String empleadoNombre,
        JuegoSummary juego,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String resultado,
        String observaciones
) {}