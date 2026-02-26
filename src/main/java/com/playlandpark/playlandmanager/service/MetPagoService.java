package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.metpago.MetPagoRequest;
import com.playlandpark.playlandmanager.model.dto.metpago.MetPagoResponse;

import java.util.List;

public interface MetPagoService {

    MetPagoResponse create(Integer idBoleta, MetPagoRequest request);

    MetPagoResponse findById(Integer idMetPago);

    List<MetPagoResponse> findAll();

    List<MetPagoResponse> findByBoleta(Integer idBoleta);

    void delete(Integer idMetPago);
}