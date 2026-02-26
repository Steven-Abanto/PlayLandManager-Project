package com.playlandpark.playlandmanager.model.dto.boletadetalle;

import java.math.BigDecimal;

public record BoletaDetalleResponse (
    Integer idBoletaDetalle,
    Integer idProducto,
    String descripcion,
    BigDecimal precio,
    Integer cantidad,
    BigDecimal descuento,
    BigDecimal subtotal
){
}
