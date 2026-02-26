package com.playlandpark.playlandmanager.model.dto.cliente;

public record ClienteBoletaResponse (
        String tipoDoc,
        String numeDoc,
        String nombre,
        String apePaterno,
        String apeMaterno,
        String correo,
        String telefono,
        String direccion
){
}
