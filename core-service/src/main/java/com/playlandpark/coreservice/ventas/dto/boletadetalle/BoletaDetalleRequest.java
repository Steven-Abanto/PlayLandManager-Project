package com.playlandpark.coreservice.ventas.dto.boletadetalle;

import java.math.BigDecimal;

public record BoletaDetalleRequest (
        Integer idProducto,
        Integer cantidad,
        BigDecimal descuento
){
}
