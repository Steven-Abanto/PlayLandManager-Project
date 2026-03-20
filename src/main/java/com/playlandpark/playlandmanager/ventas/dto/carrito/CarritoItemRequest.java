package com.playlandpark.playlandmanager.ventas.dto.carrito;

public record CarritoItemRequest(
        Integer idCarrito,
        Integer idProducto,
        Integer cantidad
) {}