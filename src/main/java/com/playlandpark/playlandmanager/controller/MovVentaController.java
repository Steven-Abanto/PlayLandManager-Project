package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.movventa.MovVentaRequest;
import com.playlandpark.playlandmanager.model.dto.movventa.MovVentaResponse;
import com.playlandpark.playlandmanager.service.MovVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mov-ventas")
@RequiredArgsConstructor
public class MovVentaController {

    private final MovVentaService movVentaService;

    @PostMapping
    public ResponseEntity<MovVentaResponse> create(@RequestBody MovVentaRequest request) {
        return ResponseEntity.ok(movVentaService.create(request));
    }

    @GetMapping("/{idMovVenta}")
    public ResponseEntity<MovVentaResponse> findById(@PathVariable Integer idMovVenta) {
        return ResponseEntity.ok(movVentaService.findById(idMovVenta));
    }

    @GetMapping
    public ResponseEntity<List<MovVentaResponse>> findAll() {
        return ResponseEntity.ok(movVentaService.findAll());
    }

    @GetMapping("/caja/{idCaja}")
    public ResponseEntity<List<MovVentaResponse>> findByCaja(@PathVariable Integer idCaja) {
        return ResponseEntity.ok(movVentaService.findByCaja(idCaja));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<MovVentaResponse>> findByFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(movVentaService.findByFecha(fecha));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<MovVentaResponse>> findByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(movVentaService.findByRangoFechas(start, end));
    }

    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovVentaResponse>> findByTipoMovimiento(@PathVariable String tipoMovimiento) {
        return ResponseEntity.ok(movVentaService.findByTipoMovimiento(tipoMovimiento));
    }

    @DeleteMapping("/{idMovVenta}")
    public ResponseEntity<Void> delete(@PathVariable Integer idMovVenta) {
        movVentaService.delete(idMovVenta);
        return ResponseEntity.noContent().build();
    }
}