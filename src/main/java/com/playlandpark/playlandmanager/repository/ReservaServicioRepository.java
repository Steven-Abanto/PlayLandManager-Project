package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.ReservaServicio;
import com.playlandpark.playlandmanager.model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaServicioRepository extends JpaRepository<ReservaServicio, Integer> {

    List<ReservaServicio> findByCliente_IdCliente(Integer idCliente);

    List<ReservaServicio> findByProducto_IdProducto(Integer idProducto);

    List<ReservaServicio> findByFechaReserva(LocalDate fechaReserva);

    List<ReservaServicio> findByFechaReservaBetween(LocalDate start, LocalDate end);

    List<ReservaServicio> findByEstado(EstadoReserva estado);

    @Query("""
        select r from ReservaServicio r
        where r.producto.idProducto = :idProducto
          and r.fechaReserva = :fecha
          and r.estado <> :estadoCancelada
          and (r.horaInicio < :horaFin and :horaInicio < r.horaFin)
    """)
    List<ReservaServicio> findOverlaps(
            @Param("idProducto") Integer idProducto,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("estadoCancelada") EstadoReserva estadoCancelada
    );
}