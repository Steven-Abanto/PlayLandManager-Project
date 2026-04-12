package com.playlandpark.authservice.auth.service.impl;

import com.playlandpark.authservice.auth.dto.auth.MeResponse;
import com.playlandpark.authservice.auth.entity.Usuario;
import com.playlandpark.authservice.auth.repository.UsuarioRepository;
import com.playlandpark.authservice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public MeResponse me(String username) {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

        return new MeResponse(
                usuario.getIdUsuario(),
                usuario.getUsuario(),
                usuario.getRol().name(),
                usuario.getActivo(),
                usuario.getIdEmpleado(),
                usuario.getIdCliente()
        );
    }
}