package com.playlandpark.authservice.auth.service.impl;

import com.playlandpark.authservice.auth.dto.auth.LoginRequest;
import com.playlandpark.authservice.auth.dto.auth.LoginResponse;
import com.playlandpark.authservice.auth.dto.auth.MeResponse;
import com.playlandpark.authservice.auth.entity.Usuario;
import com.playlandpark.authservice.auth.repository.UsuarioRepository;
import com.playlandpark.authservice.auth.security.JwtService;
import com.playlandpark.authservice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsuario(request.usuario())
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña incorrectos"));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new BadCredentialsException("Usuario inactivo");
        }

        boolean passwordValida;

        if (usuario.getContrasena() != null &&
                (usuario.getContrasena().startsWith("$2a$")
                || usuario.getContrasena().startsWith("$2b$")
                || usuario.getContrasena().startsWith("$2y$"))) {
            passwordValida = passwordEncoder.matches(request.contrasena(), usuario.getContrasena());
        } else {
            passwordValida = request.contrasena().equals(usuario.getContrasena());
        }

        if (!passwordValida) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        String token = jwtService.generateToken(
                usuario.getIdUsuario(),
                usuario.getUsuario(),
                usuario.getRol().name()
        );

        return new LoginResponse(
                token,
                "Bearer",
                600L,
                usuario.getUsuario(),
                usuario.getRol().name()
        );
    }

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