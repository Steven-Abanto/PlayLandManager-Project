package com.playlandpark.authservice.integration.core;

import com.playlandpark.authservice.client.core.CoreClient;
import com.playlandpark.authservice.integration.core.dto.ClienteCoreRequest;
import com.playlandpark.authservice.integration.core.dto.ClienteCoreResponse;
import com.playlandpark.authservice.integration.core.dto.EmpleadoCoreRequest;
import com.playlandpark.authservice.integration.core.dto.EmpleadoCoreResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
@RequiredArgsConstructor
public class CoreConsultaService {

    private final CoreClient coreClient;
    private final HttpServletRequest httpServletRequest;
    private final ObjectMapper objectMapper;

    public ClienteCoreResponse crearCliente(ClienteCoreRequest request) {
        try {
            return coreClient.crearCliente(request);
        } catch (FeignException e) {
            throw mapFeignException("crear cliente", e);
        }
    }

    public EmpleadoCoreResponse crearEmpleado(EmpleadoCoreRequest request) {
        try {
            String authorization = httpServletRequest.getHeader("Authorization");
            return coreClient.crearEmpleado(authorization, request);
        } catch (FeignException e) {
            throw mapFeignException("crear empleado", e);
        }
    }

    public ClienteCoreResponse obtenerCliente(Integer id) {
        try {
            return coreClient.obtenerCliente(id);
        } catch (FeignException e) {
            throw mapFeignException("consultar cliente", e);
        }
    }

    public EmpleadoCoreResponse obtenerEmpleado(Integer idEmpleado) {
        try {
            return coreClient.obtenerEmpleado(idEmpleado);
        } catch (FeignException e) {
            throw mapFeignException("consultar empleado", e);
        }
    }

    public void eliminarCliente(Integer id) {
        try {
            coreClient.eliminarCliente(id);
        } catch (FeignException e) {
            throw new IllegalStateException("Error al eliminar cliente en core-service");
        }
    }

    public void eliminarEmpleado(Integer id) {
        try {
            coreClient.eliminarEmpleado(id);
        } catch (FeignException e) {
            throw new IllegalStateException("Error al eliminar empleado en core-service");
        }
    }

    private RuntimeException mapFeignException(String accion, FeignException e) {
        String detail = extractDetail(e);

        return switch (e.status()) {
            case 400 -> new IllegalArgumentException(detail);
            case 401 -> new IllegalStateException("No autorizado al " + accion + " en core-service. Detalle: " + detail);
            case 403 -> new IllegalStateException("Acceso denegado al " + accion + " en core-service. Detalle: " + detail);
            case 404 -> new IllegalArgumentException(detail);
            case 409 -> new IllegalStateException(detail);
            default -> new IllegalStateException("Error al " + accion + " en core-service. HTTP status: " + e.status() + ". Detalle: " + detail);
        };
    }

    private String extractDetail(FeignException e) {
        String body = e.contentUTF8();

        if (body == null || body.isBlank()) {
            return "Sin detalle adicional";
        }

        try {
            JsonNode json = objectMapper.readTree(body);

            if (json.hasNonNull("message")) {
                return json.get("message").asText();
            }

            if (json.hasNonNull("error")) {
                return json.get("error").asText();
            }

            return body;
        } catch (Exception ex) {
            return body;
        }
    }
}