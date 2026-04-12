package com.playlandpark.coreservice.personas.dto.empleado;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EmpleadoRequest(

        @NotBlank(message = "tipoDoc es obligatorio")
        String tipoDoc,

        @NotBlank(message = "numeDoc es obligatorio")
        @Size(min = 8, max = 15, message = "numeDoc debe tener entre 8 y 15 caracteres")
        String numeDoc,

        @NotBlank(message = "nombre es obligatorio")
        String nombre,

        @NotBlank(message = "apePaterno es obligatorio")
        String apePaterno,

        String apeMaterno,

        @NotBlank(message = "genero es obligatorio")
        String genero,

        @NotNull(message = "fechaNac es obligatoria")
        @Past(message = "fechaNac debe ser una fecha pasada")
        LocalDate fechaNac,

        @NotBlank(message = "correo es obligatorio")
        @Email(message = "correo no tiene un formato válido")
        String correo,

        @NotBlank(message = "telefono es obligatorio")
        String telefono,

        @NotBlank(message = "direccion es obligatoria")
        String direccion,

        @NotNull(message = "local es obligatorio")
        Integer local,

        @NotNull(message = "idCargo es obligatorio")
        Integer idCargo,

        @NotNull(message = "fechaInicio es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin,

        Boolean activo
) {}