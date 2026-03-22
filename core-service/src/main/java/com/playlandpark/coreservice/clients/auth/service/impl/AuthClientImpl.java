package com.playlandpark.coreservice.clients.auth.service.impl;

import com.playlandpark.coreservice.clients.auth.service.AuthClient;
import com.playlandpark.coreservice.clients.auth.dto.UsuarioData;
import org.springframework.stereotype.Component;

@Component
public class AuthClientImpl implements AuthClient {

    @Override
    public UsuarioData obtenerUsuario(Integer idUsuario) {
        throw new UnsupportedOperationException("Aún no implementado");
    }
}