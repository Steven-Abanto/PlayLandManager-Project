package com.playlandpark.playlandmanager.controller;

import com.playlandpark.playlandmanager.model.dto.auth.LoginRequest;
import com.playlandpark.playlandmanager.model.dto.auth.LoginResponse;
import com.playlandpark.playlandmanager.model.dto.auth.MeResponse;
import com.playlandpark.playlandmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication.getName()));
    }
}