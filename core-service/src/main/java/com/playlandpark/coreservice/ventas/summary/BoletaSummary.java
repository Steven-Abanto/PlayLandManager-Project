package com.playlandpark.coreservice.ventas.summary;

import com.playlandpark.coreservice.ventas.enums.TipoDocuVenta;

public record BoletaSummary(
        TipoDocuVenta tipoDocuVenta,
        String numeDocuVenta
) {}
