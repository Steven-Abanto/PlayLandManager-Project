package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.usuario.UsuarioRequest;
import com.playlandpark.playlandmanager.model.dto.usuario.UsuarioResponse;
import com.playlandpark.playlandmanager.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@RequestBody UsuarioRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<UsuarioResponse> findByUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(usuarioService.findByUsuario(usuario));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponse>> findByRol(
            @PathVariable String rol,
            @RequestParam(defaultValue = "false") boolean onlyActive
    ) {
        return ResponseEntity.ok(usuarioService.findByRol(rol, onlyActive));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
            @PathVariable Integer id,
            @RequestBody UsuarioRequest request
    ) {
        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    @PatchMapping("/{id}/logic-delete")
    public ResponseEntity<Void> logicDelete(@PathVariable Integer id) {
        usuarioService.logicDelete(id);
        return ResponseEntity.noContent().build();
    }
}