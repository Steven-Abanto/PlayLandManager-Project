package com.playlandpark.coreservice.clients.auth.dto;

public record UsuarioData(
        Integer idUsuario,
        String usuario,
        String rol,
        Boolean activo,
        Integer idEmpleado,
        Integer idCliente
) {
}