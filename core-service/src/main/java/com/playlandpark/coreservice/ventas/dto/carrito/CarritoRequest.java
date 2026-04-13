package com.playlandpark.coreservice.ventas.dto.carrito;

public record CarritoRequest(
        Integer idUsuario,
        String estado,
        String tipoCompra
) {}
