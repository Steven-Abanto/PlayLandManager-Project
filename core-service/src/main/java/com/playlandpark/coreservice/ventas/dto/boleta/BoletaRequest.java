package com.playlandpark.coreservice.ventas.dto.boleta;

import com.playlandpark.coreservice.ventas.dto.boletadetalle.BoletaDetalleRequest;
import com.playlandpark.coreservice.ventas.dto.metpago.MetPagoRequest;
import com.playlandpark.coreservice.ventas.enums.TipoDocuVenta;

import java.util.List;

public record BoletaRequest(
        Integer idCaja,
        Integer idEmpleado,

        // Tipo de documento de venta (BOL, FAC, etc.)
        TipoDocuVenta tipoDocuVenta,

        // Cliente opcional (venta rápida)
        Integer idCliente,

        List<BoletaDetalleRequest> detalles,
        List<MetPagoRequest> pagos
) {
}