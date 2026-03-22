package com.playlandpark.coreservice.ventas.service;

import com.playlandpark.coreservice.ventas.dto.metpago.MetPagoRequest;
import com.playlandpark.coreservice.ventas.dto.metpago.MetPagoResponse;

import java.util.List;

public interface MetPagoService {

    MetPagoResponse create(Integer idBoleta, MetPagoRequest request);

    MetPagoResponse findById(Integer idMetPago);

    List<MetPagoResponse> findAll();

    List<MetPagoResponse> findByBoleta(Integer idBoleta);

    void delete(Integer idMetPago);
}