package com.playlandpark.coreservice.ventas.dto.boleta;

import com.playlandpark.coreservice.ventas.dto.metpago.MetPagoRequest;
import com.playlandpark.coreservice.ventas.enums.TipoDocuVenta;

import java.util.List;

public record BoletaCarritoRequest(
        Integer idCarrito,
        Integer idCaja,
        Integer idEmpleado,
        TipoDocuVenta tipoDocuVenta,
        Integer idCliente, // opcional, puede venir null
        List<MetPagoRequest> pagos
) {}