package com.playlandpark.authservice.auth.dto.usuario;

import com.playlandpark.authservice.auth.enums.RolesUsuario;
import jakarta.validation.constraints.*;

public record UsuarioRequest(

        @NotBlank(message = "usuario es obligatorio")
        String usuario,

        @NotBlank(message = "contrasena es obligatoria")
        @Size(min = 6, message = "contrasena debe tener al menos 6 caracteres")
        String contrasena,

        @NotNull(message = "rol es obligatorio")
        RolesUsuario rol,

        Integer idEmpleado,
        Integer idCliente,
        Boolean activo
) {}