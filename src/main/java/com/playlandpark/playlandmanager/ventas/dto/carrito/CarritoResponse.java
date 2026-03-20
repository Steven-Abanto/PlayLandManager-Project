package com.playlandpark.playlandmanager.ventas.dto.carrito;

import com.playlandpark.playlandmanager.personas.dto.carritodetalle.CarritoDetalleResponse;
import com.playlandpark.playlandmanager.auth.summary.UsuarioSummary;

import java.time.LocalDateTime;
import java.util.List;

public record CarritoResponse(
        Integer idCarrito,
        UsuarioSummary usuario,
        String estado,
        String codigoPromocion,
        LocalDateTime fechaCreacion,
        List<CarritoDetalleResponse> detalles
) {}