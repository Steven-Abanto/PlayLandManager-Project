package com.playlandpark.authservice.client.core;

import com.playlandpark.authservice.client.core.dto.ClienteData;
import com.playlandpark.authservice.client.core.dto.EmpleadoData;
import com.playlandpark.authservice.config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "coreClient",
        url = "${core-service.url}",
        configuration = FeignAuthConfig.class
)
public interface CoreClient {

    @GetMapping("/api/core/clientes/{id}")
    ClienteData obtenerCliente(@PathVariable("id") Integer idCliente);

    @GetMapping("/api/core/empleados/{idEmpleado}")
    EmpleadoData obtenerEmpleado(@PathVariable("idEmpleado") Integer idEmpleado);
}