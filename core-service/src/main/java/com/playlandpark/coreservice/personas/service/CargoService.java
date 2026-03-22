package com.playlandpark.coreservice.personas.service;

import com.playlandpark.coreservice.personas.dto.cargo.CargoRequest;
import com.playlandpark.coreservice.personas.dto.cargo.CargoResponse;

import java.util.List;

public interface CargoService {

    CargoResponse create(CargoRequest request);

    CargoResponse findById(Integer idCargo);

    CargoResponse findByRole(String rol);

    List<CargoResponse> findAll();

    CargoResponse update(Integer idCargo, CargoRequest request);

    void delete(Integer idCargo);
}
