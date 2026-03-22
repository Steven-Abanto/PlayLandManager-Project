package com.playlandpark.coreservice.clients.catalogo.service.impl;

import com.playlandpark.coreservice.clients.catalogo.dto.PromocionData;
import com.playlandpark.coreservice.clients.catalogo.service.CatalogoClient;
import com.playlandpark.coreservice.clients.catalogo.dto.ProductoData;
import com.playlandpark.coreservice.clients.catalogo.dto.MovInventarioRequest;
import org.springframework.stereotype.Component;

@Component
public class CatalogoClientImpl implements CatalogoClient {

    @Override
    public ProductoData obtenerProducto(Integer idProducto) {
        // Temporal
        throw new UnsupportedOperationException("Aún no implementado");
    }

    @Override
    public void registrarMovimiento(MovInventarioRequest request) {
        // Tempora
        throw new UnsupportedOperationException("Aún no implementado");
    }

    @Override
    public PromocionData obtenerPromocionPorCodigo(String codigo) {
        throw new UnsupportedOperationException("Aún no implementado");
    }
}