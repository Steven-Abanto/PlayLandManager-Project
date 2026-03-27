package com.playlandpark.authservice.client.core.dto;

import java.time.LocalDate;

public record ClienteData(
        Integer idCliente,
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
        Boolean activo
) {
}