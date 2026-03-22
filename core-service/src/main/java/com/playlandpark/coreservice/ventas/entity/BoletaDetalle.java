package com.playlandpark.coreservice.ventas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "boleta_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletaDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBoletaDetalle;

    @ManyToOne
    @JoinColumn(name = "idBoleta", nullable = false)
    private Boleta boleta;

    @Column(name = "idProducto", nullable = false)
    private Integer idProducto;

    @Column(nullable = false)
    private String descripcionProducto;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal precio = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal descuento =  BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "idPromocion")
    private Integer idPromocion;
}

