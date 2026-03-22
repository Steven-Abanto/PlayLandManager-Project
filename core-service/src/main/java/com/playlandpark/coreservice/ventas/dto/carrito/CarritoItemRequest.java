package com.playlandpark.coreservice.ventas.dto.carrito;

public record CarritoItemRequest(
        Integer idCarrito,
        Integer idProducto,
        Integer cantidad
) {}