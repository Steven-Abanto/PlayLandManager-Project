package com.playlandpark.authservice.client.core.dto;

import java.time.LocalDate;

public record EmpleadoData(
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
) {
}