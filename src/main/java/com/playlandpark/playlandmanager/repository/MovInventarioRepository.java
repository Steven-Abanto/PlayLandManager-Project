package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.MovInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovInventarioRepository extends JpaRepository<MovInventario, Integer> {

    List<MovInventario> findByBoleta_IdBoleta(Integer idBoleta);

    List<MovInventario> findByProducto_IdProducto(Integer idProducto);

    List<MovInventario> findByFecha(LocalDate fecha);

    List<MovInventario> findByFechaBetween(LocalDate start, LocalDate end);

    List<MovInventario> findByTipoMovimiento(String tipoMovimiento);
}