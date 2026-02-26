package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.CarritoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoDetalleRepository extends JpaRepository<CarritoDetalle, Integer> {

    List<CarritoDetalle> findByCarrito_IdCarrito(Integer idCarrito);

    Optional<CarritoDetalle> findByCarrito_IdCarritoAndProducto_IdProducto(Integer idCarrito, Integer idProducto);
}