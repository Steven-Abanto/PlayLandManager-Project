package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.mantenimiento.MantenimientoRequest;
import com.playlandpark.playlandmanager.model.dto.mantenimiento.MantenimientoResponse;
import com.playlandpark.playlandmanager.service.MantenimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mantenimientos")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    @PostMapping
    public ResponseEntity<MantenimientoResponse> create(@RequestBody MantenimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mantenimientoService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(mantenimientoService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<MantenimientoResponse>> findAll() {
        return ResponseEntity.ok(mantenimientoService.findAll());
    }

    @GetMapping("/employee/{idEmpleado}")
    public ResponseEntity<List<MantenimientoResponse>> findByEmployee(@PathVariable Integer idEmpleado) {
        return ResponseEntity.ok(mantenimientoService.findByEmployee(idEmpleado));
    }

    @GetMapping("/game/{idJuego}")
    public ResponseEntity<List<MantenimientoResponse>> findByGame(@PathVariable Integer idJuego) {
        return ResponseEntity.ok(mantenimientoService.findByGame(idJuego));
    }

    @GetMapping("/fecha-inicio")
    public ResponseEntity<List<MantenimientoResponse>> findByStartDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return ResponseEntity.ok(mantenimientoService.findByStartDateRange(start, end));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoResponse> update(
            @PathVariable Integer id,
            @RequestBody MantenimientoRequest request
    ) {
        return ResponseEntity.ok(mantenimientoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        mantenimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}