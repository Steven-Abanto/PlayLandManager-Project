package com.playlandpark.authservice.auth.summary;

import com.playlandpark.authservice.auth.enums.RolesUsuario;

public record UsuarioSummary(
        Integer idUsuario,
        String usuario,
        RolesUsuario rol,
        Boolean activo
) {}
