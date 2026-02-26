package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.movventa.MovVentaRequest;
import com.playlandpark.playlandmanager.model.dto.movventa.MovVentaResponse;

import java.time.LocalDate;
import java.util.List;

public interface MovVentaService {

    MovVentaResponse create(MovVentaRequest request);

    MovVentaResponse findById(Integer idMovVenta);

    List<MovVentaResponse> findAll();

    List<MovVentaResponse> findByCaja(Integer idCaja);

    List<MovVentaResponse> findByFecha(LocalDate fecha);

    List<MovVentaResponse> findByRangoFechas(LocalDate start, LocalDate end);

    List<MovVentaResponse> findByTipoMovimiento(String tipoMovimiento);

    void delete(Integer idMovVenta);
}