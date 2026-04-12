package com.playlandpark.authservice.auth.dto.registro;

public record CuentaRegistroRequest(
        String usuario,
        String contrasena
) {}