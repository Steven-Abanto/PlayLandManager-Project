package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.usuario.UsuarioRequest;
import com.playlandpark.playlandmanager.model.dto.usuario.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse create(UsuarioRequest request);

    UsuarioResponse findById(Integer id);

    UsuarioResponse findByUsuario(String usuario);

    List<UsuarioResponse> findByRol(String rol, boolean onlyActive);

    List<UsuarioResponse> findAll();

    UsuarioResponse update(Integer id, UsuarioRequest request);

    void logicDelete(Integer id);
}