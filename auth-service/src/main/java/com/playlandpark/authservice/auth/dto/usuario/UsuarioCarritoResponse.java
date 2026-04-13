package com.playlandpark.authservice.auth.dto.usuario;

public record UsuarioCarritoResponse(
        Integer idUsuario,
        String usuario,
        String rolPrincipal,
        Boolean activo,
        Integer idEmpleado,
        Integer idCliente
) {
}