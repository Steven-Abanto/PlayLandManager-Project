package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.boleta.BoletaCarritoRequest;
import com.playlandpark.playlandmanager.model.dto.boleta.BoletaRequest;
import com.playlandpark.playlandmanager.model.dto.boleta.BoletaResponse;
import com.playlandpark.playlandmanager.model.enums.TipoDocuVenta;
import com.playlandpark.playlandmanager.service.BoletaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/boletas")
@RequiredArgsConstructor
public class BoletaController {

    private final BoletaService boletaService;

    // CREATE
    @PostMapping
    public ResponseEntity<BoletaResponse> create(@RequestBody BoletaRequest request) {
        return ResponseEntity.ok(boletaService.create(request));
    }

    @PostMapping("/from-carrito")
    public ResponseEntity<BoletaResponse> createFromCarrito(@RequestBody BoletaCarritoRequest request) {
        return ResponseEntity.ok(boletaService.createFromCarrito(request));
    }

    // READ
    @GetMapping("/{id}")
    public ResponseEntity<BoletaResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(boletaService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<BoletaResponse>> findAll() {
        return ResponseEntity.ok(boletaService.findAll());
    }

    // FILTERS

    @GetMapping("/type/{tipo}")
    public ResponseEntity<List<BoletaResponse>> findByType(@PathVariable TipoDocuVenta tipo) {
        return ResponseEntity.ok(boletaService.findByType(tipo));
    }

    @GetMapping("/{tipo}/{numeDocuVenta}")
    public ResponseEntity<BoletaResponse> findByTypeAndNumber(@PathVariable TipoDocuVenta tipo,
                                                              @PathVariable String numeDocuVenta){
        return ResponseEntity.ok(boletaService.findByTypeAndNumber(tipo, numeDocuVenta));
    }

    @GetMapping("/caja/{idCaja}")
    public ResponseEntity<List<BoletaResponse>> findByCaja(@PathVariable Integer idCaja) {
        return ResponseEntity.ok(boletaService.findByCaja(idCaja));
    }

    @GetMapping("/employee/{idEmpleado}")
    public ResponseEntity<List<BoletaResponse>> findByEmployee(@PathVariable Integer idEmpleado) {
        return ResponseEntity.ok(boletaService.findByEmployee(idEmpleado));
    }

    @GetMapping("/client/{idCliente}")
    public ResponseEntity<List<BoletaResponse>> findByClient(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(boletaService.findByClient(idCliente));
    }

    // Date Range: /api/boletas/date-range?start=2026-02-22T00:00:00&end=2026-02-22T23:59:59
    @GetMapping("/date-range")
    public ResponseEntity<List<BoletaResponse>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(boletaService.findByDateRange(start, end));
    }

    // CANCEL
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        boletaService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}