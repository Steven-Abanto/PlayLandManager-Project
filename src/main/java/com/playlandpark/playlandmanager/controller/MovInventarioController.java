package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.movinventario.MovInventarioRequest;
import com.playlandpark.playlandmanager.model.dto.movinventario.MovInventarioResponse;
import com.playlandpark.playlandmanager.service.MovInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mov-inventario")
@RequiredArgsConstructor
public class MovInventarioController {

    private final MovInventarioService movInventarioService;

    @PostMapping
    public ResponseEntity<MovInventarioResponse> create(@RequestBody MovInventarioRequest request) {
        return ResponseEntity.ok(movInventarioService.create(request));
    }

    @GetMapping("/{idMovInv}")
    public ResponseEntity<MovInventarioResponse> findById(@PathVariable Integer idMovInv) {
        return ResponseEntity.ok(movInventarioService.findById(idMovInv));
    }

    @GetMapping
    public ResponseEntity<List<MovInventarioResponse>> findAll() {
        return ResponseEntity.ok(movInventarioService.findAll());
    }

    @GetMapping("/boleta/{idBoleta}")
    public ResponseEntity<List<MovInventarioResponse>> findByBoleta(@PathVariable Integer idBoleta) {
        return ResponseEntity.ok(movInventarioService.findByBoleta(idBoleta));
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<MovInventarioResponse>> findByProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(movInventarioService.findByProducto(idProducto));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<MovInventarioResponse>> findByFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(movInventarioService.findByFecha(fecha));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<MovInventarioResponse>> findByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(movInventarioService.findByRangoFechas(start, end));
    }

    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovInventarioResponse>> findByTipoMovimiento(@PathVariable String tipoMovimiento) {
        return ResponseEntity.ok(movInventarioService.findByTipoMovimiento(tipoMovimiento));
    }

    @DeleteMapping("/{idMovInv}")
    public ResponseEntity<Void> delete(@PathVariable Integer idMovInv) {
        movInventarioService.delete(idMovInv);
        return ResponseEntity.noContent().build();
    }
}