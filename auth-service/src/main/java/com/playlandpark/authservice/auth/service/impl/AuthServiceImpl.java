package com.playlandpark.authservice.auth.service.impl;

import com.playlandpark.authservice.auth.dto.auth.KeycloakTokenResponse;
import com.playlandpark.authservice.auth.dto.auth.LoginRequest;
import com.playlandpark.authservice.auth.dto.auth.LoginResponse;
import com.playlandpark.authservice.auth.dto.auth.MeResponse;
import com.playlandpark.authservice.auth.entity.Usuario;
import com.playlandpark.authservice.auth.repository.UsuarioRepository;
import com.playlandpark.authservice.auth.service.AuthService;
import com.playlandpark.authservice.config.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final KeycloakProperties keycloakProperties;
    private final ObjectMapper objectMapper;

    private final RestClient restClient = RestClient.create();

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

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        KeycloakTokenResponse tokenResponse = requestPasswordToken(request.usuario(), request.contrasena());
        MeResponse me = me(request.usuario());

        if (Boolean.FALSE.equals(me.activo())) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo");
        }

        return new LoginResponse(
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                tokenResponse.tokenType(),
                tokenResponse.expiresIn(),
                me.username(),
                me.roles(),
                me.rolPrincipal(),
                me.idUsuario(),
                me.idEmpleado(),
                me.idCliente(),
                me.activo()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse refresh(String refreshToken) {
        KeycloakTokenResponse tokenResponse = requestRefreshToken(refreshToken);
        String username = extractUsernameFromAccessToken(tokenResponse.accessToken());
        MeResponse me = me(username);

        if (Boolean.FALSE.equals(me.activo())) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo");
        }

        return new LoginResponse(
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                tokenResponse.tokenType(),
                tokenResponse.expiresIn(),
                me.username(),
                me.roles(),
                me.rolPrincipal(),
                me.idUsuario(),
                me.idEmpleado(),
                me.idCliente(),
                me.activo()
        );
    }

    private KeycloakTokenResponse requestPasswordToken(String username, String password) {
        try {
            LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("client_id", keycloakProperties.getLogin().getClientId());
            formData.add("client_secret", keycloakProperties.getLogin().getClientSecret());
            formData.add("username", username);
            formData.add("password", password);

            return restClient.post()
                    .uri(tokenUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(KeycloakTokenResponse.class);

        } catch (RestClientResponseException ex) {
            throw new IllegalArgumentException("Error Keycloak login: " + ex.getResponseBodyAsString());
        }
    }

    private KeycloakTokenResponse requestRefreshToken(String refreshToken) {
        try {
            LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("client_id", keycloakProperties.getLogin().getClientId());
            formData.add("client_secret", keycloakProperties.getLogin().getClientSecret());
            formData.add("refresh_token", refreshToken);

            return restClient.post()
                    .uri(tokenUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(KeycloakTokenResponse.class);

        } catch (RestClientResponseException ex) {
            throw new IllegalArgumentException("Error Keycloak refresh: " + ex.getResponseBodyAsString());
        }
    }

    private String tokenUrl() {
        return keycloakProperties.getServerUrl()
                + "/realms/" + keycloakProperties.getRealm()
                + "/protocol/openid-connect/token";
    }

    private String extractUsernameFromAccessToken(String accessToken) {
        try {
            String[] parts = accessToken.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Token inválido");
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode json = objectMapper.readTree(payload);

            JsonNode preferredUsername = json.get("preferred_username");
            if (preferredUsername == null || preferredUsername.isNull()) {
                throw new IllegalArgumentException("El token no contiene preferred_username");
            }

            return preferredUsername.asText();
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo extraer el usuario del token");
        }
    }
}