package com.playlandpark.authservice.auth.dto.auth;

import java.util.List;

public record MeResponse(
        String username,
        List<String> roles,
        String rolPrincipal,
        Integer idUsuario,
        Integer idEmpleado,
        Integer idCliente,
        Boolean activo
) {}