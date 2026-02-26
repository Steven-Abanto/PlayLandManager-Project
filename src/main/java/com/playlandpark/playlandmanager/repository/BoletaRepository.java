package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.Boleta;
import com.playlandpark.playlandmanager.model.enums.TipoDocuVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoletaRepository extends JpaRepository<Boleta, Integer> {

    Optional<Boleta> findByNumeDocuVenta(String numeDocuVenta);

    Optional<Boleta> findByTipoDocuVentaAndNumeDocuVenta(TipoDocuVenta tipoDocuVenta, String numeDocuVenta);

    Optional<Boleta> findTopByTipoDocuVentaOrderByIdBoletaDesc(TipoDocuVenta tipoDocuVenta);

    boolean existsByTipoDocuVentaAndNumeDocuVenta(TipoDocuVenta tipoDocuVenta, String numeDocuVenta);

    List<Boleta> findByTipoDocuVenta(TipoDocuVenta tipoDocuVenta);

    List<Boleta> findByCaja_IdCaja(Integer idCaja);

    List<Boleta> findByEmpleado_IdEmpleado(Integer idEmpleado);

    List<Boleta> findByCliente_IdCliente(Integer idCliente);

    List<Boleta> findByFechaHoraBetween(LocalDateTime start, LocalDateTime end);
}