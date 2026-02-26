package com.playlandpark.playlandmanager.model.dto.boleta;

import com.playlandpark.playlandmanager.model.dto.boletadetalle.BoletaDetalleResponse;
import com.playlandpark.playlandmanager.model.dto.caja.CajaBoletaResponse;
import com.playlandpark.playlandmanager.model.dto.cliente.ClienteBoletaResponse;
import com.playlandpark.playlandmanager.model.dto.empleado.EmpleadoBoletaResponse;
import com.playlandpark.playlandmanager.model.dto.metpago.MetPagoResponse;
import com.playlandpark.playlandmanager.model.enums.TipoDocuVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BoletaResponse(
        Integer idBoleta,
        TipoDocuVenta tipoDocuVenta,
        String numeDocuVenta,
        BigDecimal subtotal,
        BigDecimal dsctoTotal,
        BigDecimal impuesto,
        BigDecimal total,
        BigDecimal vuelto,
        String estado,
        LocalDateTime fechaHora,
        CajaBoletaResponse caja,
        EmpleadoBoletaResponse empleado,
        ClienteBoletaResponse cliente, // null si venta rápida
        List<MetPagoResponse> pagos,
        List<BoletaDetalleResponse> detalles
) {
}