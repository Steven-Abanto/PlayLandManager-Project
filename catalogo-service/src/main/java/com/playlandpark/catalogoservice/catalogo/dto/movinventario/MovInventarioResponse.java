package com.playlandpark.catalogoservice.catalogo.dto.movinventario;

import java.time.LocalDate;

public record MovInventarioResponse(
        Integer idMovInv,
        Integer idBoleta, // puede ser null si es AJUSTE manual
        MovInventarioProductoResponse producto,
        Integer cantidad,
        LocalDate fecha,
        String tipoMovimiento
) {}
