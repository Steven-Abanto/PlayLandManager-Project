package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.JuegosUso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JuegosUsoRepository extends JpaRepository<JuegosUso, Integer> {

    List<JuegosUso> findByJuego_IdJuego(Integer idJuego);

    List<JuegosUso> findByFechaUso(LocalDate fechaUso);

    List<JuegosUso> findByJuego_IdJuegoAndFechaUso(Integer idJuego, LocalDate fechaUso);
}
