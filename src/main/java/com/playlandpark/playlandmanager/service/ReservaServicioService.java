package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.reservaservicio.ReservaServicioRequest;
import com.playlandpark.playlandmanager.model.dto.reservaservicio.ReservaServicioResponse;
import com.playlandpark.playlandmanager.model.enums.EstadoReserva;

import java.time.LocalDate;
import java.util.List;

public interface ReservaServicioService {
    ReservaServicioResponse create(ReservaServicioRequest request);

    ReservaServicioResponse findById(Integer idReserva);

    List<ReservaServicioResponse> findAll();

    List<ReservaServicioResponse> findByCliente(Integer idCliente);

    List<ReservaServicioResponse> findByProducto(Integer idProducto);

    List<ReservaServicioResponse> findByFecha(LocalDate fecha);

    List<ReservaServicioResponse> findByRangoFechas(LocalDate start, LocalDate end);

    List<ReservaServicioResponse> findByEstado(EstadoReserva estado);

    ReservaServicioResponse update(Integer idReserva, ReservaServicioRequest request);

    void cancel(Integer idReserva, String motivo);

    void delete(Integer idReserva);
}