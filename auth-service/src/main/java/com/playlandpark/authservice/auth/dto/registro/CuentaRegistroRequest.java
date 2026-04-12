package com.playlandpark.authservice.auth.dto.registro;

import jakarta.validation.constraints.*;

public record CuentaRegistroRequest(

        @NotBlank(message = "usuario es obligatorio")
        String usuario,

        @NotBlank(message = "contrasena es obligatoria")
        @Size(min = 6, message = "contrasena debe tener al menos 6 caracteres")
        String contrasena
) {}