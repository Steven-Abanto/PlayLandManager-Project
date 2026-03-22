package com.playlandpark.coreservice.operacion.repository;

import com.playlandpark.coreservice.operacion.entity.Juego;
import com.playlandpark.coreservice.operacion.enums.EstadoJuego;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JuegoRepository extends JpaRepository<Juego, Integer> {

    Optional<Juego> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Juego> findByActivoTrue();

    List<Juego> findByEstado(EstadoJuego estado);

    List<Juego> findByEstadoAndActivoTrue(EstadoJuego estado);
}
