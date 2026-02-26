package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    Optional<Carrito> findFirstByUsuario_IdUsuarioAndEstado(Integer idUsuario, String estado);
}