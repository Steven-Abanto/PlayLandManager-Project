package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.movinventario.MovInventarioRequest;
import com.playlandpark.playlandmanager.model.dto.movinventario.MovInventarioResponse;

import java.time.LocalDate;
import java.util.List;

public interface MovInventarioService {

    MovInventarioResponse create(MovInventarioRequest request);

    MovInventarioResponse findById(Integer idMovInv);

    List<MovInventarioResponse> findAll();

    List<MovInventarioResponse> findByBoleta(Integer idBoleta);

    List<MovInventarioResponse> findByProducto(Integer idProducto);

    List<MovInventarioResponse> findByFecha(LocalDate fecha);

    List<MovInventarioResponse> findByRangoFechas(LocalDate start, LocalDate end);

    List<MovInventarioResponse> findByTipoMovimiento(String tipoMovimiento);

    void delete(Integer idMovInv);
}
