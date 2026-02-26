package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.metpago.MetPagoRequest;
import com.playlandpark.playlandmanager.model.dto.metpago.MetPagoResponse;
import com.playlandpark.playlandmanager.service.MetPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class MetPagoController {

    private final MetPagoService metPagoService;

    // Crear pago para una boleta: POST /api/pagos/boleta/{idBoleta}
    @PostMapping("/boleta/{idBoleta}")
    public ResponseEntity<MetPagoResponse> create(
            @PathVariable Integer idBoleta,
            @RequestBody MetPagoRequest request
    ) {
        return ResponseEntity.ok(metPagoService.create(idBoleta, request));
    }

    // Obtener pago por id
    @GetMapping("/{idMetPago}")
    public ResponseEntity<MetPagoResponse> findById(@PathVariable Integer idMetPago) {
        return ResponseEntity.ok(metPagoService.findById(idMetPago));
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<MetPagoResponse>> findAll() {
        return ResponseEntity.ok(metPagoService.findAll());
    }

    // Listar pagos por boleta
    @GetMapping("/boleta/{idBoleta}")
    public ResponseEntity<List<MetPagoResponse>> findByBoleta(@PathVariable Integer idBoleta) {
        return ResponseEntity.ok(metPagoService.findByBoleta(idBoleta));
    }

    // Eliminar pago
    @DeleteMapping("/{idMetPago}")
    public ResponseEntity<Void> delete(@PathVariable Integer idMetPago) {
        metPagoService.delete(idMetPago);
        return ResponseEntity.noContent().build();
    }
}