package com.playlandpark.authservice.auth.dto.registro;

import java.time.LocalDate;

public record ClienteRegistroRequest(
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
        CuentaRegistroRequest cuenta
) {}