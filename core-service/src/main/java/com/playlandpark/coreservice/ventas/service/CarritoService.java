package com.playlandpark.coreservice.ventas.service;

import com.playlandpark.coreservice.ventas.dto.carrito.CarritoDescuentoRequest;
import com.playlandpark.coreservice.ventas.dto.carrito.CarritoItemRequest;
import com.playlandpark.coreservice.ventas.dto.carrito.CarritoRequest;
import com.playlandpark.coreservice.ventas.dto.carrito.CarritoResponse;

import java.util.List;

public interface CarritoService {

    List<CarritoResponse> findAllActiveCart();

    CarritoResponse getOrCreateActiveCart(CarritoRequest request);

    CarritoResponse findById(Integer idCarrito);

    CarritoResponse addItem(CarritoItemRequest request);

    CarritoResponse updateItemQuantity(CarritoItemRequest request);

    CarritoResponse removeItem(Integer idCarrito, Integer idProducto);

    CarritoResponse applyPromotion(Integer idCarrito, CarritoDescuentoRequest request);

    CarritoResponse removePromotion(Integer idCarrito);

    CarritoResponse recalculate(Integer idCarrito);
}