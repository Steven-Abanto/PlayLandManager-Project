package com.playlandpark.playlandmanager.ventas.dto.boleta;

import com.playlandpark.playlandmanager.ventas.dto.metpago.MetPagoRequest;
import com.playlandpark.playlandmanager.ventas.enums.TipoDocuVenta;

import java.util.List;

public record BoletaCarritoRequest(
        Integer idCarrito,
        Integer idCaja,
        Integer idEmpleado,
        TipoDocuVenta tipoDocuVenta,
        Integer idCliente, // opcional, puede venir null
        List<MetPagoRequest> pagos
) {}