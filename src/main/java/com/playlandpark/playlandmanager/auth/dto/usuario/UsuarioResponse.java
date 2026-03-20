package com.playlandpark.playlandmanager.auth.dto.usuario;

import com.playlandpark.playlandmanager.personas.summary.ClienteSummary;
import com.playlandpark.playlandmanager.personas.summary.EmpleadoSummary;
import com.playlandpark.playlandmanager.auth.enums.RolesUsuario;

public record UsuarioResponse(
        Integer idUsuario,
        String usuario,
        RolesUsuario rol,
        Boolean activo,
        EmpleadoSummary empleado,
        ClienteSummary cliente
) {}