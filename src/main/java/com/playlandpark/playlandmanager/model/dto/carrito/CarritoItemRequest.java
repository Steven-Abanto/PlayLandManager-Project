package com.playlandpark.playlandmanager.model.dto.carrito;

public record CarritoItemRequest(
        Integer idCarrito,
        Integer idProducto,
        Integer cantidad
) {}