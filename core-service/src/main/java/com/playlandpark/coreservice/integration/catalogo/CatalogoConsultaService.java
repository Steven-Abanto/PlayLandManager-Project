package com.playlandpark.coreservice.integration.catalogo;

import com.playlandpark.coreservice.client.catalogo.CatalogoClient;
import com.playlandpark.coreservice.client.catalogo.dto.MovInventarioRequest;
import com.playlandpark.coreservice.client.catalogo.dto.ProductoData;
import com.playlandpark.coreservice.client.catalogo.dto.PromocionData;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogoConsultaService {

    private final CatalogoClient catalogoClient;

    public ProductoData obtenerProducto(Integer idProducto) {
        try {
            return catalogoClient.obtenerProducto(idProducto);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Producto no encontrado en catálogo: " + idProducto);
        } catch (FeignException.Unauthorized e) {
            throw new IllegalStateException("No autorizado para consultar catalogo-service");
        } catch (FeignException.Forbidden e) {
            throw new IllegalStateException("Acceso denegado al consultar catalogo-service");
        } catch (FeignException e) {
            System.out.println("ERROR REAL:");
            System.out.println(e.contentUTF8());

            throw new IllegalStateException(
                    "Error al consultar catalogo-service. HTTP status: " + e.status()
            );
        }
    }

    public PromocionData obtenerPromocionPorCodigo(String codigo) {
        try {
            return catalogoClient.obtenerPromocionPorCodigo(codigo);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Código de promoción no encontrado: " + codigo);
        } catch (FeignException.Unauthorized e) {
            throw new IllegalStateException("No autorizado para consultar catalogo-service");
        } catch (FeignException.Forbidden e) {
            throw new IllegalStateException("Acceso denegado al consultar catalogo-service");
        } catch (FeignException e) {
            System.out.println("ERROR REAL:");
            System.out.println(e.contentUTF8());

            throw new IllegalStateException(
                    "Error al consultar catalogo-service. HTTP status: " + e.status()
            );
        }
    }

    public void registrarMovimiento(MovInventarioRequest request) {
        try {
            catalogoClient.registrarMovimiento(request);
        } catch (FeignException.Unauthorized e) {
            throw new IllegalStateException("No autorizado para registrar movimiento en catalogo-service");
        } catch (FeignException.Forbidden e) {
            throw new IllegalStateException("Acceso denegado al registrar movimiento en catalogo-service");
        } catch (FeignException e) {
            System.out.println("ERROR REAL:");
            System.out.println(e.contentUTF8());

            throw new IllegalStateException(
                    "Error al registrar movimiento en catalogo-service. HTTP status: " + e.status()
            );
        }
    }
}