package com.playlandpark.coreservice.ventas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrito;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;

    @Column(nullable = false)
    private String estado;

    @Column(name = "tipo_compra", nullable = false)
    private String tipoCompra;

    @Column(name = "codigo_promocion")
    private String codigoPromocion;

    @Column(name = "idPromocion")
    private Integer idPromocion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoDetalle> detalles = new ArrayList<>();
}