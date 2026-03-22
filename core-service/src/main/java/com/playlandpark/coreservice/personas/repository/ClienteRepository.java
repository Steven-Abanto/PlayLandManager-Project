package com.playlandpark.coreservice.personas.repository;

import com.playlandpark.coreservice.personas.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByCorreoIgnoreCase(String correo);
    Optional<Cliente> findByNumeDoc(String numeDoc);

    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByNumeDoc(String numeDoc);
    boolean existsByTelefono(String telefono);

    List<Cliente> findByActivoTrue();
}