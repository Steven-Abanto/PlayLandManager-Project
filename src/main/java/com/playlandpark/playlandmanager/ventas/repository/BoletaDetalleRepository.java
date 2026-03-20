package com.playlandpark.playlandmanager.ventas.repository;

import com.playlandpark.playlandmanager.ventas.entity.BoletaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoletaDetalleRepository extends JpaRepository<BoletaDetalle, Integer> {

    List<BoletaDetalle> findByBoleta_IdBoleta(Integer idBoleta);

    List<BoletaDetalle> findByProducto_IdProducto(Integer idProducto);

    List<BoletaDetalle> findByPromocion_IdPromocion(Integer idPromocion);
}