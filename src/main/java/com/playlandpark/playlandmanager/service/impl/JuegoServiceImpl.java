package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.juego.JuegoRequest;
import com.playlandpark.playlandmanager.model.dto.juego.JuegoResponse;
import com.playlandpark.playlandmanager.model.entity.Juego;
import com.playlandpark.playlandmanager.model.enums.EstadoJuego;
import com.playlandpark.playlandmanager.repository.JuegoRepository;
import com.playlandpark.playlandmanager.service.JuegoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JuegoServiceImpl implements JuegoService {

    private final JuegoRepository juegoRepository;

    // Crea un nuevo juego a partir de un request
    @Override
    public JuegoResponse create(JuegoRequest request) {
        validateRequest(request);

        if (juegoRepository.existsByCodigo(request.codigo())) {
            throw new IllegalArgumentException("Ya existe un juego con el código: " + request.codigo());
        }

        Juego juego = new Juego();
        applyRequest(juego, request);

        if (juego.getActivo() == null) juego.setActivo(true);

        Juego saved = juegoRepository.save(juego);
        return mapToResponse(saved);
    }

    // Busca un juego por su id
    @Override
    @Transactional(readOnly = true)
    public JuegoResponse findById(Integer idJuego) {
        return mapToResponse(getJuego(idJuego));
    }

    // Busca un juego por su código
    @Override
    @Transactional(readOnly = true)
    public JuegoResponse findByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank())
            throw new IllegalArgumentException("El código es obligatorio.");

        Juego juego = juegoRepository.findByCodigo(codigo.trim())
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado con código: " + codigo));

        return mapToResponse(juego);
    }

    // Busca todos los juegos, con opción de filtrar solo los activos
    @Override
    @Transactional(readOnly = true)
    public List<JuegoResponse> findAll(boolean onlyActive) {
        List<Juego> list = onlyActive
                ? juegoRepository.findByActivoTrue()
                : juegoRepository.findAll();

        return list.stream().map(this::mapToResponse).toList();
    }

    // Busca juegos por estado, con opción de filtrar solo los activos
    @Override
    @Transactional(readOnly = true)
    public List<JuegoResponse> findByEstado(EstadoJuego estado, boolean onlyActive) {

        if (estado == null)
            throw new IllegalArgumentException("El estado es obligatorio.");

        List<Juego> list = onlyActive
                ? juegoRepository.findByEstadoAndActivoTrue(estado)
                : juegoRepository.findByEstado(estado);

        return list.stream().map(this::mapToResponse).toList();
    }

    // Actualiza un juego existente
    @Override
    public JuegoResponse update(Integer idJuego, JuegoRequest request) {

        Juego juego = getJuego(idJuego);

        if (request.codigo() != null &&
                !request.codigo().equals(juego.getCodigo()) &&
                juegoRepository.existsByCodigo(request.codigo())) {
            throw new IllegalArgumentException("Ya existe un juego con el código: " + request.codigo());
        }

        applyRequest(juego, request);

        return mapToResponse(juegoRepository.save(juego));
    }

    // Elimina un juego lógicamente de la base de datos
    @Override
    public void logicDelete(Integer idJuego) {
        Juego juego = getJuego(idJuego);
        juego.setActivo(false);
        juegoRepository.save(juego);
    }

    // -------------------- helpers --------------------
    // Obtiene un juego por id
    private Juego getJuego(Integer id) {
        return juegoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado con id: " + id));
    }

    // Valida los datos del request
    private void validateRequest(JuegoRequest request) {
        if (request == null)
            throw new IllegalArgumentException("El request no puede ser nulo.");

        if (request.codigo() == null || request.codigo().isBlank())
            throw new IllegalArgumentException("El código es obligatorio.");

        if (request.estado() == null)
            throw new IllegalArgumentException("El estado es obligatorio.");

        if (request.ultMant() == null || request.proxMant() == null)
            throw new IllegalArgumentException("Las fechas de mantenimiento son obligatorias.");

        if (request.proxMant().isBefore(request.ultMant()))
            throw new IllegalArgumentException("La fecha de próximo mantenimiento no puede ser menor que la última.");
    }

    // Aplica los datos del request a la entidad
    private void applyRequest(Juego juego, JuegoRequest request) {

        if (request.codigo() != null) juego.setCodigo(request.codigo());
        if (request.nombre() != null) juego.setNombre(request.nombre());
        if (request.tipo() != null) juego.setTipo(request.tipo());
        if (request.descripcion() != null) juego.setDescripcion(request.descripcion());
        if (request.estado() != null) juego.setEstado(request.estado());

        if (request.ultMant() != null) juego.setUltMant(request.ultMant());
        if (request.proxMant() != null) juego.setProxMant(request.proxMant());

        if (request.activo() != null) juego.setActivo(request.activo());

        // Validación adicional al actualizar fechas
        if (juego.getProxMant().isBefore(juego.getUltMant())) {
            throw new IllegalArgumentException("La fecha de próximo mantenimiento no puede ser menor que la última.");
        }
    }

    // Mapea una entidad Juego a un DTO de respuesta
    private JuegoResponse mapToResponse(Juego j) {
        return new JuegoResponse(
                j.getIdJuego(),
                j.getCodigo(),
                j.getNombre(),
                j.getTipo(),
                j.getDescripcion(),
                j.getEstado(),
                j.getUltMant(),
                j.getProxMant(),
                j.getActivo()
        );
    }
}