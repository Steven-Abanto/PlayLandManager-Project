package com.playlandpark.playlandmanager.personas.summary;

public record ClienteSummary(
        Integer idCliente,
        String tipoDoc,
        String numeDoc,
        String nombre,
        String apePaterno,
        String apeMaterno,
        String correo
) {}
