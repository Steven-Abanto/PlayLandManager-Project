package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.boletadetalle.BoletaDetalleRequest;
import com.playlandpark.playlandmanager.model.dto.boletadetalle.BoletaDetalleResponse;

import java.util.List;

public interface BoletaDetalleService {

    BoletaDetalleResponse create(Integer idBoleta, BoletaDetalleRequest request);

    BoletaDetalleResponse findById(Integer idBoletaDetalle);

    List<BoletaDetalleResponse> findAll();

    List<BoletaDetalleResponse> findByBoleta(Integer idBoleta);

    List<BoletaDetalleResponse> findByProducto(Integer idProducto);

    List<BoletaDetalleResponse> findByPromocion(Integer idPromocion);

    BoletaDetalleResponse update(Integer idBoletaDetalle, BoletaDetalleRequest request);

    void delete(Integer idBoletaDetalle);
}