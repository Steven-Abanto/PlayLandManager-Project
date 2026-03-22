package com.playlandpark.coreservice.ventas.dto.carrito;

import com.playlandpark.coreservice.ventas.dto.metpago.MetPagoRequest;

import java.util.List;

public record CarritoCheckoutRequest(
        Integer idCarrito,
        Integer idCaja,
        Integer idEmpleado,   // obligatorio
        Integer idCliente,    // opcional (puede venir null)
        List<MetPagoRequest> pagos
) {}

//Respuesta: BoletaResponse