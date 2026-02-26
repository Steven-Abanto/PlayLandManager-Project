package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.reservaservicio.ReservaServicioRequest;
import com.playlandpark.playlandmanager.model.dto.reservaservicio.ReservaServicioResponse;
import com.playlandpark.playlandmanager.model.enums.EstadoReserva;
import com.playlandpark.playlandmanager.service.ReservaServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas-servicio")
@RequiredArgsConstructor
public class ReservaServicioController {

    private final ReservaServicioService reservaServicioService;

    @PostMapping
    public ResponseEntity<ReservaServicioResponse> create(@RequestBody ReservaServicioRequest request) {
        return ResponseEntity.ok(reservaServicioService.create(request));
    }

    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaServicioResponse> findById(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reservaServicioService.findById(idReserva));
    }

    @GetMapping
    public ResponseEntity<List<ReservaServicioResponse>> findAll() {
        return ResponseEntity.ok(reservaServicioService.findAll());
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<ReservaServicioResponse>> findByCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(reservaServicioService.findByCliente(idCliente));
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<ReservaServicioResponse>> findByProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(reservaServicioService.findByProducto(idProducto));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<ReservaServicioResponse>> findByFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(reservaServicioService.findByFecha(fecha));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<ReservaServicioResponse>> findByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(reservaServicioService.findByRangoFechas(start, end));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaServicioResponse>> findByEstado(@PathVariable EstadoReserva estado) {
        return ResponseEntity.ok(reservaServicioService.findByEstado(estado));
    }

    @PutMapping("/{idReserva}")
    public ResponseEntity<ReservaServicioResponse> update(
            @PathVariable Integer idReserva,
            @RequestBody ReservaServicioRequest request
    ) {
        return ResponseEntity.ok(reservaServicioService.update(idReserva, request));
    }

    // Cancel con motivo: /api/reservas-servicio/{id}/cancel?motivo=...
    @PatchMapping("/{idReserva}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable Integer idReserva,
            @RequestParam(required = false) String motivo
    ) {
        reservaServicioService.cancel(idReserva, motivo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idReserva}")
    public ResponseEntity<Void> delete(@PathVariable Integer idReserva) {
        reservaServicioService.delete(idReserva);
        return ResponseEntity.noContent().build();
    }
}