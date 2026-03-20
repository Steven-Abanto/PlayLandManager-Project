package com.playlandpark.playlandmanager.common.dto.summary;

import java.time.LocalDateTime;

public record CarritoSummary(
        Integer idCarrito,
        String estado,
        LocalDateTime fechaCreacion
) {}
