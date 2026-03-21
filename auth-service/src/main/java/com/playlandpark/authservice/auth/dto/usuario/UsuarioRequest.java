package com.playlandpark.authservice.auth.dto.usuario;

import com.playlandpark.authservice.auth.enums.RolesUsuario;

public record UsuarioRequest(
        String usuario,
        String contrasena,
        RolesUsuario rol,
        Integer idEmpleado,
        Integer idCliente,
        Boolean activo
) {}