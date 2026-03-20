package com.playlandpark.playlandmanager.auth.dto.usuario;

import com.playlandpark.playlandmanager.common.dto.summary.ClienteSummary;
import com.playlandpark.playlandmanager.common.dto.summary.EmpleadoSummary;
import com.playlandpark.playlandmanager.auth.enums.RolesUsuario;

public record UsuarioResponse(
        Integer idUsuario,
        String usuario,
        RolesUsuario rol,
        Boolean activo,
        EmpleadoSummary empleado,
        ClienteSummary cliente
) {}