package com.playlandpark.coreservice.ventas.summary;

import java.time.LocalDateTime;

public record CarritoSummary(
        Integer idCarrito,
        String estado,
        LocalDateTime fechaCreacion
) {}
