package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.mantenimiento.MantenimientoRequest;
import com.playlandpark.playlandmanager.model.dto.mantenimiento.MantenimientoResponse;

import java.time.LocalDate;
import java.util.List;

public interface MantenimientoService {

    MantenimientoResponse create(MantenimientoRequest request);

    MantenimientoResponse findById(Integer id);

    List<MantenimientoResponse> findAll();

    List<MantenimientoResponse> findByEmployee(Integer idEmpleado);

    List<MantenimientoResponse> findByGame(Integer idJuego);

    List<MantenimientoResponse> findByStartDateRange(LocalDate start, LocalDate end);

    MantenimientoResponse update(Integer id, MantenimientoRequest request);

    void delete(Integer id);
}