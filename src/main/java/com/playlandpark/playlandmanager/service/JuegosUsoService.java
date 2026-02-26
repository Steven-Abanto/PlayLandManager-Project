package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.juegosuso.JuegosUsoRequest;
import com.playlandpark.playlandmanager.model.dto.juegosuso.JuegosUsoResponse;

import java.time.LocalDate;
import java.util.List;

public interface JuegosUsoService {

    JuegosUsoResponse create(JuegosUsoRequest request);

    JuegosUsoResponse findById(Integer id);

    List<JuegosUsoResponse> findAll();

    List<JuegosUsoResponse> findByGame(Integer idJuego);

    List<JuegosUsoResponse> findByDate(LocalDate fechaUso);

    List<JuegosUsoResponse> findByGameAndDate(Integer idJuego, LocalDate fechaUso);

    JuegosUsoResponse update(Integer id, JuegosUsoRequest request);

    void delete(Integer id);
}