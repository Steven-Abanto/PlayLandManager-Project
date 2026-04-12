package com.playlandpark.authservice.client.core;

import com.playlandpark.authservice.integration.core.dto.ClienteCoreRequest;
import com.playlandpark.authservice.integration.core.dto.ClienteCoreResponse;
import com.playlandpark.authservice.integration.core.dto.EmpleadoCoreRequest;
import com.playlandpark.authservice.integration.core.dto.EmpleadoCoreResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "coreClient",
        url = "${core-service.url}"
)
public interface CoreClient {

    @PostMapping("/api/core/clientes")
    ClienteCoreResponse crearCliente(@RequestBody ClienteCoreRequest request);

    @PostMapping("/api/core/empleados")
    EmpleadoCoreResponse crearEmpleado(
            @RequestHeader("Authorization") String authorization,
            @RequestBody EmpleadoCoreRequest request
    );

    @GetMapping("/api/core/clientes/{id}")
    ClienteCoreResponse obtenerCliente(@PathVariable("id") Integer id);

    @GetMapping("/api/core/empleados/{idEmpleado}")
    EmpleadoCoreResponse obtenerEmpleado(@PathVariable("idEmpleado") Integer idEmpleado);

    @PatchMapping("/api/core/clientes/delete/{id}")
    void eliminarCliente(@PathVariable("id") Integer id);

    @PatchMapping("/api/core/empleados/{id}/logic-delete")
    void eliminarEmpleado(@PathVariable("id") Integer id);
}