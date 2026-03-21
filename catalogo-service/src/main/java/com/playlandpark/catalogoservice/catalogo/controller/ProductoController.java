package com.playlandpark.catalogoservice.catalogo.controller;

import com.playlandpark.catalogoservice.catalogo.dto.producto.ProductoRequest;
import com.playlandpark.catalogoservice.catalogo.dto.producto.ProductoResponse;
import com.playlandpark.catalogoservice.catalogo.summary.ProductoSummary;
import com.playlandpark.catalogoservice.catalogo.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponse> create(@RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductoResponse> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productoService.findBySku(sku));
    }

    @GetMapping("/upc/{upc}")
    public ResponseEntity<ProductoResponse> findByUpc(@PathVariable String upc) {
        return ResponseEntity.ok(productoService.findByUpc(upc));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> findAll(
            @RequestParam(defaultValue = "false") boolean onlyActive
    ) {
        return ResponseEntity.ok(productoService.findAll(onlyActive));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<ProductoSummary>> findAllSummary(
            @RequestParam(defaultValue = "false") boolean onlyActive
    ) {
        return ResponseEntity.ok(productoService.findAllSummary(onlyActive));
    }

    @GetMapping("/type")
    public ResponseEntity<List<ProductoResponse>> findByServiceType(
            @RequestParam(defaultValue = "false") boolean esServicio,
            @RequestParam(defaultValue = "false") boolean onlyActive
    ) {
        return ResponseEntity.ok(productoService.findByServiceType(esServicio, onlyActive));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(
            @PathVariable Integer id,
            @RequestBody ProductoRequest request
    ) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<Void> logicDelete(@PathVariable Integer id) {
        productoService.logicDelete(id);
        return ResponseEntity.noContent().build();
    }
}