package com.playlandpark.coreservice.ventas.repository;

import com.playlandpark.coreservice.ventas.entity.BoletaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoletaDetalleRepository extends JpaRepository<BoletaDetalle, Integer> {

    List<BoletaDetalle> findByBoleta_IdBoleta(Integer idBoleta);

    List<BoletaDetalle> findByIdProducto(Integer idProducto);

    List<BoletaDetalle> findByIdPromocion(Integer idPromocion);
}