package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.juegosuso.JuegosUsoRequest;
import com.playlandpark.playlandmanager.model.dto.juegosuso.JuegosUsoResponse;
import com.playlandpark.playlandmanager.service.JuegosUsoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/juegos-uso")
@RequiredArgsConstructor
public class JuegosUsoController {

    private final JuegosUsoService juegosUsoService;

    @PostMapping
    public ResponseEntity<JuegosUsoResponse> create(@RequestBody JuegosUsoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(juegosUsoService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JuegosUsoResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(juegosUsoService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<JuegosUsoResponse>> findAll() {
        return ResponseEntity.ok(juegosUsoService.findAll());
    }

    @GetMapping("/juego/{idJuego}")
    public ResponseEntity<List<JuegosUsoResponse>> findByGame(@PathVariable Integer idJuego) {
        return ResponseEntity.ok(juegosUsoService.findByGame(idJuego));
    }

    @GetMapping("/fecha/{fechaUso}")
    public ResponseEntity<List<JuegosUsoResponse>> findByDate(@PathVariable LocalDate fechaUso) {
        return ResponseEntity.ok(juegosUsoService.findByDate(fechaUso));
    }

    @GetMapping("/juego/{idJuego}/fecha/{fechaUso}")
    public ResponseEntity<List<JuegosUsoResponse>> findByGameAndDate(
            @PathVariable Integer idJuego,
            @PathVariable LocalDate fechaUso
    ) {
        return ResponseEntity.ok(juegosUsoService.findByGameAndDate(idJuego, fechaUso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JuegosUsoResponse> update(
            @PathVariable Integer id,
            @RequestBody JuegosUsoRequest request
    ) {
        return ResponseEntity.ok(juegosUsoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        juegosUsoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}