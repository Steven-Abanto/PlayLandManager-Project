package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.carrito.CarritoDescuentoRequest;
import com.playlandpark.playlandmanager.model.dto.carrito.CarritoItemRequest;
import com.playlandpark.playlandmanager.model.dto.carrito.CarritoResponse;
import com.playlandpark.playlandmanager.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @PostMapping("/active/{idUsuario}")
    public ResponseEntity<CarritoResponse> getOrCreateActiveCart(@PathVariable Integer idUsuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.getOrCreateActiveCart(idUsuario));
    }

    @GetMapping("/{idCarrito}")
    public ResponseEntity<CarritoResponse> findById(@PathVariable Integer idCarrito) {
        return ResponseEntity.ok(carritoService.findById(idCarrito));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoResponse> addItem(@RequestBody CarritoItemRequest request) {
        return ResponseEntity.ok(carritoService.addItem(request));
    }

    @PutMapping("/items")
    public ResponseEntity<CarritoResponse> updateItemQuantity(@RequestBody CarritoItemRequest request) {
        return ResponseEntity.ok(carritoService.updateItemQuantity(request));
    }

    @DeleteMapping("/{idCarrito}/items/{idProducto}")
    public ResponseEntity<CarritoResponse> removeItem(
            @PathVariable Integer idCarrito,
            @PathVariable Integer idProducto
    ) {
        return ResponseEntity.ok(carritoService.removeItem(idCarrito, idProducto));
    }

    @PostMapping("/{idCarrito}/promotion")
    public ResponseEntity<CarritoResponse> applyPromotion(@PathVariable Integer idCarrito,
                                                          @RequestBody CarritoDescuentoRequest request) {
        return ResponseEntity.ok(carritoService.applyPromotion(idCarrito, request));
    }

    @DeleteMapping("/{idCarrito}/promotion")
    public ResponseEntity<CarritoResponse> removePromotion(@PathVariable Integer idCarrito) {
        return ResponseEntity.ok(carritoService.removePromotion(idCarrito));
    }

    @PostMapping("/{idCarrito}/recalculate")
    public ResponseEntity<CarritoResponse> recalculate(@PathVariable Integer idCarrito) {
        return ResponseEntity.ok(carritoService.recalculate(idCarrito));
    }
}