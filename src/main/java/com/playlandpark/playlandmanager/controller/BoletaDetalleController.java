package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.boletadetalle.BoletaDetalleRequest;
import com.playlandpark.playlandmanager.model.dto.boletadetalle.BoletaDetalleResponse;
import com.playlandpark.playlandmanager.service.BoletaDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boleta-detalles")
@RequiredArgsConstructor
public class BoletaDetalleController {

    private final BoletaDetalleService boletaDetalleService;

    @PostMapping("/boleta/{idBoleta}")
    public ResponseEntity<BoletaDetalleResponse> create(
            @PathVariable Integer idBoleta,
            @RequestBody BoletaDetalleRequest request
    ) {
        return ResponseEntity.ok(boletaDetalleService.create(idBoleta, request));
    }

    @GetMapping("/{idBoletaDetalle}")
    public ResponseEntity<BoletaDetalleResponse> findById(@PathVariable Integer idBoletaDetalle) {
        return ResponseEntity.ok(boletaDetalleService.findById(idBoletaDetalle));
    }

    @GetMapping
    public ResponseEntity<List<BoletaDetalleResponse>> findAll() {
        return ResponseEntity.ok(boletaDetalleService.findAll());
    }

    // FILTERS
    @GetMapping("/boleta/{idBoleta}")
    public ResponseEntity<List<BoletaDetalleResponse>> findByBoleta(@PathVariable Integer idBoleta) {
        return ResponseEntity.ok(boletaDetalleService.findByBoleta(idBoleta));
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<BoletaDetalleResponse>> findByProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(boletaDetalleService.findByProducto(idProducto));
    }

    @GetMapping("/promocion/{idPromocion}")
    public ResponseEntity<List<BoletaDetalleResponse>> findByPromocion(@PathVariable Integer idPromocion) {
        return ResponseEntity.ok(boletaDetalleService.findByPromocion(idPromocion));
    }

    // UPDATE
    @PutMapping("/{idBoletaDetalle}")
    public ResponseEntity<BoletaDetalleResponse> update(
            @PathVariable Integer idBoletaDetalle,
            @RequestBody BoletaDetalleRequest request
    ) {
        return ResponseEntity.ok(boletaDetalleService.update(idBoletaDetalle, request));
    }

    // DELETE
    @DeleteMapping("/{idBoletaDetalle}")
    public ResponseEntity<Void> delete(@PathVariable Integer idBoletaDetalle) {
        boletaDetalleService.delete(idBoletaDetalle);
        return ResponseEntity.noContent().build();
    }
}