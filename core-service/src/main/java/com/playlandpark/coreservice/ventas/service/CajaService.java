package com.playlandpark.coreservice.ventas.service;

import com.playlandpark.coreservice.ventas.dto.caja.CajaAperturaRequest;
import com.playlandpark.coreservice.ventas.dto.caja.CajaCierreRequest;
import com.playlandpark.coreservice.ventas.dto.caja.CajaResponse;

import java.util.List;

public interface CajaService {

    CajaResponse open(CajaAperturaRequest request);

    CajaResponse close(CajaCierreRequest request);

    CajaResponse findById(Integer idCaja);

    CajaResponse findByCode(String codCaja);

    CajaResponse findOpenByUsuario(String usuApertura);

    CajaResponse findOnlineCaja();

    List<CajaResponse> findAll();

    List<CajaResponse> findByStatus(String estado);
}