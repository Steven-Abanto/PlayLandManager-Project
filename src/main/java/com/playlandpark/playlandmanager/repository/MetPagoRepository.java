package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.MetPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface MetPagoRepository extends JpaRepository<MetPago, Integer> {

    List<MetPago> findByBoleta_IdBoleta(Integer idBoleta);

    @Query("select coalesce(sum(p.monto), 0) from MetPago p where p.boleta.idBoleta = :idBoleta")
    BigDecimal sumMontoByBoleta(@Param("idBoleta") Integer idBoleta);
}