package com.playlandpark.playlandmanager.catalogo.dto.movinventario;

import com.playlandpark.playlandmanager.common.dto.summary.BoletaSummary;

import java.time.LocalDate;

public record MovInventarioResponse(
        Integer idMovInv,
        BoletaSummary boleta, // puede ser null si es AJUSTE manual
        MovInventarioProductoResponse producto,
        Integer cantidad,
        LocalDate fecha,
        String tipoMovimiento
) {}
