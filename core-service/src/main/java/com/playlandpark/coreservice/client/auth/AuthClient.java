package com.playlandpark.coreservice.client.auth;

import com.playlandpark.coreservice.client.auth.dto.UsuarioData;
import com.playlandpark.coreservice.config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "authClient",
        url = "${auth-service.url}",
        configuration = FeignAuthConfig.class
)
public interface AuthClient {
    @GetMapping("/api/auth/usuarios/{id}")
    UsuarioData obtenerUsuario(@PathVariable("id") Integer idUsuario);
}