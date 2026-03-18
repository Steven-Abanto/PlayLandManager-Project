package com.playlandpark.playlandmanager.model.dto.auth;

public record MeResponse(
        Integer idUsuario,
        String usuario,
        String rol,
        Boolean activo,
        Integer idEmpleado,
        Integer idCliente
) {}