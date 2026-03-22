package com.playlandpark.coreservice.ventas.repository;

import com.playlandpark.coreservice.ventas.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    Optional<Carrito> findFirstByIdUsuarioAndEstado(Integer idUsuario, String estado);
}