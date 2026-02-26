package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.boleta.BoletaCarritoRequest;
import com.playlandpark.playlandmanager.model.dto.boleta.BoletaRequest;
import com.playlandpark.playlandmanager.model.dto.boleta.BoletaResponse;
import com.playlandpark.playlandmanager.model.enums.TipoDocuVenta;

import java.time.LocalDateTime;
import java.util.List;

public interface BoletaService {

    BoletaResponse create(BoletaRequest request);

    BoletaResponse createFromCarrito(BoletaCarritoRequest request);

    BoletaResponse findById(Integer id);

    BoletaResponse findByTypeAndNumber(TipoDocuVenta tipo, String numeDocuVenta);

    List<BoletaResponse> findAll();

    List<BoletaResponse> findByType(TipoDocuVenta tipo);

    List<BoletaResponse> findByCaja(Integer idCaja);

    List<BoletaResponse> findByEmployee(Integer idEmpleado);

    List<BoletaResponse> findByClient(Integer idCliente);

    List<BoletaResponse> findByDateRange(LocalDateTime start, LocalDateTime end);

    void cancel(Integer id);
}