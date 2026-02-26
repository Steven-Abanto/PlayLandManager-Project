package com.playlandpark.playlandmanager.model.dto.summary;

import com.playlandpark.playlandmanager.model.enums.TipoDocuVenta;

public record BoletaSummary(
        TipoDocuVenta tipoDocuVenta,
        String numeDocuVenta
) {}
