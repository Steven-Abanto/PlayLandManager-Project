package com.playlandpark.playlandmanager.model.entity;

import com.playlandpark.playlandmanager.model.enums.TipoDocuVenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "boleta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBoleta;

    @ManyToOne
    @JoinColumn(name = "idCaja", nullable = false)
    private Caja caja;

    @ManyToOne
    @JoinColumn(name = "idEmpleado", nullable = false)
    private Empleado empleado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocuVenta tipoDocuVenta;

    @Column(nullable = false)
    private String numeDocuVenta;

    @ManyToOne
    @JoinColumn(name = "idCliente")
    private Cliente cliente;

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL)
    private List<MetPago> pagos;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal dsctoTotal = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal impuesto = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal vuelto = BigDecimal.ZERO;
    
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoletaDetalle> detalles;
}

