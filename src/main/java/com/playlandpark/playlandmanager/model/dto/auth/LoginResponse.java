package com.playlandpark.playlandmanager.model.dto.auth;

public record LoginResponse (
        String token,
        String tipo,
        Long expiraEn,
        String usuario,
        String rol
){
}