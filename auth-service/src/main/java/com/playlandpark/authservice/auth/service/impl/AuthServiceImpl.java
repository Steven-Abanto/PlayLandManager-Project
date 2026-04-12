package com.playlandpark.authservice.auth.service.impl;

import com.playlandpark.authservice.auth.dto.auth.MeResponse;
import com.playlandpark.authservice.auth.entity.Usuario;
import com.playlandpark.authservice.auth.repository.UsuarioRepository;
import com.playlandpark.authservice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public MeResponse me(String username) {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

        String rolPrincipal = usuario.getRol().name();
        return new MeResponse(
                usuario.getUsuario(),
                List.of(rolPrincipal),
                rolPrincipal,
                usuario.getIdUsuario(),
                usuario.getIdEmpleado(),
                usuario.getIdCliente(),
                usuario.getActivo()
        );
    }
}