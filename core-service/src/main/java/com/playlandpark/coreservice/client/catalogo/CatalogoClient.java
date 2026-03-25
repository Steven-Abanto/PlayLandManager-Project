package com.playlandpark.coreservice.client.catalogo;

import com.playlandpark.coreservice.client.catalogo.dto.MovInventarioRequest;
import com.playlandpark.coreservice.client.catalogo.dto.ProductoData;
import com.playlandpark.coreservice.client.catalogo.dto.PromocionData;
import com.playlandpark.coreservice.config.FeignAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "catalogoClient",
        url = "${catalogo-service.url}",
        configuration = FeignAuthConfig.class
)
public interface CatalogoClient {

    @GetMapping("/api/catalogo/productos/{id}")
    ProductoData obtenerProducto(@PathVariable("id") Integer idProducto);

    @GetMapping("/api/catalogo/promociones/codigo/{codigo}")
    PromocionData obtenerPromocionPorCodigo(@PathVariable("codigo") String codigoPromocion);

    @PostMapping("/api/catalogo/mov-inventario")
    void registrarMovimiento(@RequestBody MovInventarioRequest request);
}