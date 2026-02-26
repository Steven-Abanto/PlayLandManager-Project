package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.carrito.CarritoDescuentoRequest;
import com.playlandpark.playlandmanager.model.dto.carrito.CarritoItemRequest;
import com.playlandpark.playlandmanager.model.dto.carrito.CarritoResponse;

public interface CarritoService {

    CarritoResponse getOrCreateActiveCart(Integer idUsuario);

    CarritoResponse findById(Integer idCarrito);

    CarritoResponse addItem(CarritoItemRequest request);

    CarritoResponse updateItemQuantity(CarritoItemRequest request);

    CarritoResponse removeItem(Integer idCarrito, Integer idProducto);

    CarritoResponse applyPromotion(Integer idCarrito, CarritoDescuentoRequest request);

    CarritoResponse removePromotion(Integer idCarrito);

    CarritoResponse recalculate(Integer idCarrito);
}