package com.playlandpark.authservice.integration.core.dto;

import java.time.LocalDate;

public record EmpleadoCoreRequest(
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
        Integer idCargo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean activo
) {}