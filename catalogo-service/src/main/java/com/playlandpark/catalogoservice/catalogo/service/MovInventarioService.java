package com.playlandpark.catalogoservice.catalogo.service;

import com.playlandpark.catalogoservice.catalogo.dto.movinventario.MovInventarioRequest;
import com.playlandpark.catalogoservice.catalogo.dto.movinventario.MovInventarioResponse;

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
