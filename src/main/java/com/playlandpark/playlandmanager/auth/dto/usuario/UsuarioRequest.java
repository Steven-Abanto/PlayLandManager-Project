package com.playlandpark.playlandmanager.auth.dto.usuario;

import com.playlandpark.playlandmanager.auth.enums.RolesUsuario;

public record UsuarioRequest(
        String usuario,
        String contrasena,
        RolesUsuario rol,
        Integer idEmpleado,
        Integer idCliente,
        Boolean activo
) {}