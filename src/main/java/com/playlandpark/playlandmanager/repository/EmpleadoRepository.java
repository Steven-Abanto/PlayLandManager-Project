package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    Optional<Empleado> findByNumeDoc(String numeDoc);

    Optional<Empleado> findByCorreo(String correo);

    Optional<Empleado> findByTelefono(String telefono);

    boolean existsByNumeDoc(String numeDoc);

    boolean existsByCorreo(String correo);

    boolean existsByTelefono(String telefono);

    List<Empleado> findByActivoTrue();

    List<Empleado> findByLocal(Integer local);

    List<Empleado> findByLocalAndActivoTrue(Integer local);

    List<Empleado> findByCargo_IdCargo(Integer idCargo);

    List<Empleado> findByCargo_IdCargoAndActivoTrue(Integer idCargo);
}