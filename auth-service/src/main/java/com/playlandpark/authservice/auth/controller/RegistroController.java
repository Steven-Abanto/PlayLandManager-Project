package com.playlandpark.authservice.auth.controller;

import com.playlandpark.authservice.auth.dto.registro.ClienteRegistroRequest;
import com.playlandpark.authservice.auth.dto.registro.EmpleadoRegistroRequest;
import com.playlandpark.authservice.auth.dto.usuario.UsuarioResponse;
import com.playlandpark.authservice.auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/registro")
@RequiredArgsConstructor
public class RegistroController {

    private final UsuarioService usuarioService;

    @PostMapping("/cliente")
    public ResponseEntity<UsuarioResponse> registrarCliente(@RequestBody ClienteRegistroRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.registrarClienteCompleto(request));
    }

    @PostMapping("/empleado")
    public ResponseEntity<UsuarioResponse> registrarEmpleado(@RequestBody EmpleadoRegistroRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.registrarEmpleadoCompleto(request));
    }
}