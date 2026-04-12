package com.playlandpark.authservice.auth.dto.registro;

import java.time.LocalDate;

public record EmpleadoRegistroRequest(
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
        CuentaRegistroRequest cuenta
) {}