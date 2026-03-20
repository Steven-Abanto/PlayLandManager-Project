package com.playlandpark.playlandmanager.auth.summary;

import com.playlandpark.playlandmanager.auth.enums.RolesUsuario;

public record UsuarioSummary(
        Integer idUsuario,
        String usuario,
        RolesUsuario rol,
        Boolean activo
) {}
