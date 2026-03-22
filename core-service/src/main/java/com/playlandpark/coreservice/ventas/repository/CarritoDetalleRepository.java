package com.playlandpark.coreservice.ventas.repository;

import com.playlandpark.coreservice.ventas.entity.CarritoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoDetalleRepository extends JpaRepository<CarritoDetalle, Integer> {

    List<CarritoDetalle> findByCarrito_IdCarrito(Integer idCarrito);

    Optional<CarritoDetalle> findByCarrito_IdCarritoAndIdProducto(Integer idCarrito, Integer idProducto);
}