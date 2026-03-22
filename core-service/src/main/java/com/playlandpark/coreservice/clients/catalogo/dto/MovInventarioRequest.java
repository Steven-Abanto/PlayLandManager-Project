package com.playlandpark.coreservice.clients.catalogo.dto;

import java.time.LocalDate;

public record MovInventarioRequest(
        Integer idBoleta,
        Integer idProducto,
        Integer cantidad,
        LocalDate fecha,
        String tipoMovimiento
) {
}