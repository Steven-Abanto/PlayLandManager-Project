package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.empleado.EmpleadoRequest;
import com.playlandpark.playlandmanager.model.dto.empleado.EmpleadoResponse;
import com.playlandpark.playlandmanager.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<EmpleadoResponse> create(@RequestBody EmpleadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(empleadoService.findById(id));
    }

    @GetMapping("/document/{numeDoc}")
    public ResponseEntity<EmpleadoResponse> findByDocument(@PathVariable String numeDoc) {
        return ResponseEntity.ok(empleadoService.findByDocument(numeDoc));
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoResponse>> findAll(
            @RequestParam(defaultValue = "false") boolean onlyActive
    ) {
        return ResponseEntity.ok(empleadoService.findAll(onlyActive));
    }

    @GetMapping("/local/{local}")
    public ResponseEntity<List<EmpleadoResponse>> findByLocal(
            @PathVariable Integer local,
            @RequestParam(defaultValue = "false") boolean onlyActive
    ) {
        return ResponseEntity.ok(empleadoService.findByLocal(local, onlyActive));
    }

    @GetMapping("/cargo/{idCargo}")
    public ResponseEntity<List<EmpleadoResponse>> findByCargo(
            @PathVariable Integer idCargo,
            @RequestParam(defaultValue = "false") boolean onlyActive
    ) {
        return ResponseEntity.ok(empleadoService.findByCargo(idCargo, onlyActive));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> update(
            @PathVariable Integer id,
            @RequestBody EmpleadoRequest request
    ) {
        return ResponseEntity.ok(empleadoService.update(id, request));
    }

    @PatchMapping("/{id}/logic-delete")
    public ResponseEntity<Void> logicDelete(@PathVariable Integer id) {
        empleadoService.logicDelete(id);
        return ResponseEntity.noContent().build();
    }
}