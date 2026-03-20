package com.playlandpark.playlandmanager.ventas.dto.boleta;

import com.playlandpark.playlandmanager.ventas.dto.boletadetalle.BoletaDetalleRequest;
import com.playlandpark.playlandmanager.ventas.dto.metpago.MetPagoRequest;
import com.playlandpark.playlandmanager.ventas.enums.TipoDocuVenta;

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