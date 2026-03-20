package com.playlandpark.playlandmanager.catalogo.service;

import com.playlandpark.playlandmanager.catalogo.dto.promocion.PromocionRequest;
import com.playlandpark.playlandmanager.catalogo.dto.promocion.PromocionResponse;
import com.playlandpark.playlandmanager.catalogo.summary.PromocionSummary;

import java.util.List;

public interface PromocionService {

    PromocionResponse create(PromocionRequest request);

    PromocionResponse findById(Integer idPromocion);

    PromocionResponse findByCodigo(String codigo);

    List<PromocionResponse> findAll(boolean onlyActive);

    List<PromocionSummary> findAllSummary(boolean onlyActive);

    List<PromocionResponse> findActiveToday();

    PromocionResponse update(Integer idPromocion, PromocionRequest request);

    void logicDelete(Integer idPromocion);

    PromocionResponse addProducto(Integer idPromocion, Integer idProducto);

    PromocionResponse removeProducto(Integer idPromocion, Integer idProducto);
}
