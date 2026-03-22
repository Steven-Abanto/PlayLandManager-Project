package com.playlandpark.coreservice.personas.summary;

public record ClienteSummary(
        Integer idCliente,
        String tipoDoc,
        String numeDoc,
        String nombre,
        String apePaterno,
        String apeMaterno,
        String correo
) {}
