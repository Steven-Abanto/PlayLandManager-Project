package com.playlandpark.playlandmanager.personas.service;

import com.playlandpark.playlandmanager.personas.dto.cliente.ClienteRequest;
import com.playlandpark.playlandmanager.personas.dto.cliente.ClienteResponse;
import com.playlandpark.playlandmanager.personas.summary.ClienteSummary;

import java.util.List;

public interface ClienteService {

    ClienteResponse create(ClienteRequest request);

    ClienteResponse findById(Integer idCliente);

    ClienteResponse findByDocument(String numeDoc);

    ClienteResponse findByEmail(String correo);

    List<ClienteResponse> findAll(boolean onlyActive);

    List<ClienteSummary> findAllSummary(boolean onlyActive);

    ClienteResponse update(Integer idCliente, ClienteRequest request);

    void logicDelete(Integer idCliente); // activo = false
}