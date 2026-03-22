package com.playlandpark.coreservice.operacion.service;

import com.playlandpark.coreservice.operacion.dto.juego.JuegoRequest;
import com.playlandpark.coreservice.operacion.dto.juego.JuegoResponse;
import com.playlandpark.coreservice.operacion.enums.EstadoJuego;

import java.util.List;

public interface JuegoService {

    JuegoResponse create(JuegoRequest request);

    JuegoResponse findById(Integer idJuego);

    JuegoResponse findByCodigo(String codigo);

    List<JuegoResponse> findAll(boolean onlyActive);

    List<JuegoResponse> findByEstado(EstadoJuego estado, boolean onlyActive);

    JuegoResponse update(Integer idJuego, JuegoRequest request);

    void logicDelete(Integer idJuego);
}
