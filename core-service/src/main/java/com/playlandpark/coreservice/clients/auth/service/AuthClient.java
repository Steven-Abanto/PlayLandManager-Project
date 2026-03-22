package com.playlandpark.coreservice.clients.auth.service;

import com.playlandpark.coreservice.clients.auth.dto.UsuarioData;

public interface AuthClient {
    UsuarioData obtenerUsuario(Integer idUsuario);
}