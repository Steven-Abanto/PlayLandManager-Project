package com.playlandpark.playlandmanager.common.dto.summary;

import com.playlandpark.playlandmanager.ventas.enums.TipoDocuVenta;

public record BoletaSummary(
        TipoDocuVenta tipoDocuVenta,
        String numeDocuVenta
) {}
