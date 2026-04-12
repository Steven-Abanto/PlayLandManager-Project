package com.playlandpark.authservice.auth.service;

import com.playlandpark.authservice.auth.dto.registro.ClienteRegistroRequest;
import com.playlandpark.authservice.auth.dto.registro.EmpleadoRegistroRequest;
import com.playlandpark.authservice.auth.dto.usuario.UsuarioRequest;
import com.playlandpark.authservice.auth.dto.usuario.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse create(UsuarioRequest request);

    UsuarioResponse registrarClienteCompleto(ClienteRegistroRequest request);

    UsuarioResponse registrarEmpleadoCompleto(EmpleadoRegistroRequest request);

    UsuarioResponse findById(Integer id);

    UsuarioResponse findByUsuario(String usuario);

    List<UsuarioResponse> findByRol(String rol, boolean onlyActive);

    List<UsuarioResponse> findAll();

    UsuarioResponse update(Integer id, UsuarioRequest request);

    void logicDelete(Integer id);
}