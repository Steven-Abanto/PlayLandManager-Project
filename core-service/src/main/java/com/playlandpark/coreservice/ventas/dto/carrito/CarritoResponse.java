package com.playlandpark.coreservice.ventas.dto.carrito;

import com.playlandpark.coreservice.ventas.dto.carritodetalle.CarritoDetalleResponse;

import java.time.LocalDateTime;
import java.util.List;

public record CarritoResponse(
        Integer idCarrito,
        Integer idUsuario,
        String estado,
        String codigoPromocion,
        LocalDateTime fechaCreacion,
        List<CarritoDetalleResponse> detalles
) {}