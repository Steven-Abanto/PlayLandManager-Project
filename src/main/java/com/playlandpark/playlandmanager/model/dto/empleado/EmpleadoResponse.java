package com.playlandpark.playlandmanager.model.dto.empleado;

import java.time.LocalDate;

public record EmpleadoResponse(
        Integer idEmpleado,
        String tipoDoc,
        String numeDoc,
        String nombre,
        String apePaterno,
        String apeMaterno,
        String genero,
        LocalDate fechaNac,
        String correo,
        String telefono,
        String direccion,
        Integer local,
        String cargo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean activo
) {}
