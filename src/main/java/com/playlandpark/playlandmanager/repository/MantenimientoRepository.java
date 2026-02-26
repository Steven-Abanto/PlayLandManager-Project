package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Integer> {

    List<Mantenimiento> findByEmpleado_IdEmpleado(Integer idEmpleado);

    List<Mantenimiento> findByJuego_IdJuego(Integer idJuego);

    List<Mantenimiento> findByFechaInicioBetween(LocalDate start, LocalDate end);

    List<Mantenimiento> findByFechaFinBetween(LocalDate start, LocalDate end);

    List<Mantenimiento> findByJuego_IdJuegoAndFechaInicioBetween(Integer idJuego, LocalDate start, LocalDate end);
}