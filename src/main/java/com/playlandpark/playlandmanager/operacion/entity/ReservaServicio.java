package com.playlandpark.playlandmanager.operacion.entity;

import com.playlandpark.playlandmanager.catalogo.entity.Producto;
import com.playlandpark.playlandmanager.personas.entity.Cliente;
import com.playlandpark.playlandmanager.operacion.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "ReservaServicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    @ManyToOne
    @JoinColumn(name = "idProducto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDate fechaReserva;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    private Integer cntPersonas;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    private String observaciones;
}

