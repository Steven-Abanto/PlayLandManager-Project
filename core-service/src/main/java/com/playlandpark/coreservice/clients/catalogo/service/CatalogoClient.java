package com.playlandpark.coreservice.clients.catalogo.service;

import com.playlandpark.coreservice.clients.catalogo.dto.ProductoData;
import com.playlandpark.coreservice.clients.catalogo.dto.MovInventarioRequest;
import com.playlandpark.coreservice.clients.catalogo.dto.PromocionData;

public interface CatalogoClient {

    ProductoData obtenerProducto(Integer idProducto);

    PromocionData obtenerPromocionPorCodigo(String  codigoPromocion);

    void registrarMovimiento(MovInventarioRequest request);
}