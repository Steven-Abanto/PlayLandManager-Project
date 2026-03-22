package com.playlandpark.coreservice.personas.summary;

public record EmpleadoSummary(
        Integer idEmpleado,
        String tipoDoc,
        String numeDoc,
        String nombre,
        String apePaterno,
        String apeMaterno,
        String correo,
        String cargo
) {}
