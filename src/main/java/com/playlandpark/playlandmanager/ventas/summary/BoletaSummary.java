package com.playlandpark.playlandmanager.ventas.summary;

import com.playlandpark.playlandmanager.ventas.enums.TipoDocuVenta;

public record BoletaSummary(
        TipoDocuVenta tipoDocuVenta,
        String numeDocuVenta
) {}
