package com.playlandpark.playlandmanager.model.dto.movinventario;

import java.time.LocalDate;

public record MovInventarioResponse(
        Integer idMovInv,
        BoletaSummary boleta, // puede ser null si es AJUSTE manual
        MovInventarioProductoResponse producto,
        Integer cantidad,
        LocalDate fecha,
        String tipoMovimiento
) {}
