package com.playlandpark.catalogoservice.catalogo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleTestController {

    @GetMapping("/api/catalogo/admin/ping")
    public ResponseEntity<String> adminPing() {
        return ResponseEntity.ok("admin ok");
    }
}
