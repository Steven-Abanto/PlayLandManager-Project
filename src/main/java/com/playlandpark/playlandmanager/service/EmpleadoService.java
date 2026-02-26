package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.empleado.EmpleadoRequest;
import com.playlandpark.playlandmanager.model.dto.empleado.EmpleadoResponse;

import java.util.List;

public interface EmpleadoService {

    EmpleadoResponse create(EmpleadoRequest request);

    EmpleadoResponse findById(Integer idEmpleado);

    EmpleadoResponse findByDocument(String numeDoc);

    List<EmpleadoResponse> findAll(boolean onlyActive);

    List<EmpleadoResponse> findByLocal(Integer local, boolean onlyActive);

    List<EmpleadoResponse> findByCargo(Integer idCargo, boolean onlyActive);

    EmpleadoResponse update(Integer idEmpleado, EmpleadoRequest request);

    void logicDelete(Integer idEmpleado);
}