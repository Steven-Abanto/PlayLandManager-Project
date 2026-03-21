package com.playlandpark.authservice.auth.dto.usuario;

import com.playlandpark.authservice.auth.enums.RolesUsuario;

public record UsuarioResponse(
        Integer idUsuario,
        String usuario,
        RolesUsuario rol,
        Boolean activo,
        Integer idEmpleado,
        Integer idCliente
) {}