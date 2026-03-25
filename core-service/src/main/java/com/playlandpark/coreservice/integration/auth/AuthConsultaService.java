package com.playlandpark.coreservice.integration.auth;

import com.playlandpark.coreservice.client.auth.AuthClient;
import com.playlandpark.coreservice.client.auth.dto.UsuarioData;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthConsultaService {

    private final AuthClient authClient;

    public UsuarioData obtenerUsuario(Integer idUsuario) {
        try {
            return authClient.obtenerUsuario(idUsuario);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Usuario no encontrado en auth-service: " + idUsuario);
        } catch (FeignException.Unauthorized e) {
            throw new IllegalStateException("No autorizado para consultar auth-service");
        } catch (FeignException.Forbidden e) {
            throw new IllegalStateException("Acceso denegado al consultar auth-service");
        } catch (FeignException e) {
            throw new IllegalStateException(
                    "Error al consultar auth-service. HTTP status: " + e.status()
            );
        }
    }
}