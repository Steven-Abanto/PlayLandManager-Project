package com.playlandpark.coreservice.personas.repository;

import com.playlandpark.coreservice.personas.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Integer> {
    Optional<Cargo> findByRol(String rol);
    boolean existsByRol(String rol);
}