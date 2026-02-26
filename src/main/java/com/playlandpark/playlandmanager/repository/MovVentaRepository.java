package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.MovVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovVentaRepository extends JpaRepository<MovVenta, Integer> {

    List<MovVenta> findByCaja_IdCaja(Integer idCaja);

    List<MovVenta> findByFecha(LocalDate fecha);

    List<MovVenta> findByFechaBetween(LocalDate start, LocalDate end);

    List<MovVenta> findByTipoMovimiento(String tipoMovimiento);

    List<MovVenta> findByCaja_IdCajaAndFechaBetween(Integer idCaja, LocalDate start, LocalDate end);
}