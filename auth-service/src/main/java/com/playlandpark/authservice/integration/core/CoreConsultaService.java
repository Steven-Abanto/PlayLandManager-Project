package com.playlandpark.authservice.integration.core;

import com.playlandpark.authservice.client.core.CoreClient;
import com.playlandpark.authservice.client.core.dto.ClienteData;
import com.playlandpark.authservice.client.core.dto.EmpleadoData;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreConsultaService {

    private final CoreClient coreClient;

    public ClienteData obtenerCliente(Integer idCliente) {
        try {
            return coreClient.obtenerCliente(idCliente);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Cliente no encontrado en core-service: " + idCliente);
        } catch (FeignException.Unauthorized e) {
            throw new IllegalStateException("No autorizado para consultar core-service");
        } catch (FeignException.Forbidden e) {
            throw new IllegalStateException("Acceso denegado al consultar core-service");
        } catch (FeignException e) {
            throw new IllegalStateException("Error al consultar core-service. HTTP status: " + e.status());
        }
    }

    public EmpleadoData obtenerEmpleado(Integer idEmpleado) {
        try {
            return coreClient.obtenerEmpleado(idEmpleado);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Empleado no encontrado en core-service: " + idEmpleado);
        } catch (FeignException.Unauthorized e) {
            throw new IllegalStateException("No autorizado para consultar core-service");
        } catch (FeignException.Forbidden e) {
            throw new IllegalStateException("Acceso denegado al consultar core-service");
        } catch (FeignException e) {
            throw new IllegalStateException("Error al consultar core-service. HTTP status: " + e.status());
        }
    }
}