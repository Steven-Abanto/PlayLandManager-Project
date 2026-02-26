package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.juegosuso.JuegosUsoRequest;
import com.playlandpark.playlandmanager.model.dto.juegosuso.JuegosUsoResponse;
import com.playlandpark.playlandmanager.model.dto.summary.JuegoSummary;
import com.playlandpark.playlandmanager.model.entity.Juego;
import com.playlandpark.playlandmanager.model.entity.JuegosUso;
import com.playlandpark.playlandmanager.repository.JuegoRepository;
import com.playlandpark.playlandmanager.repository.JuegosUsoRepository;
import com.playlandpark.playlandmanager.service.JuegosUsoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JuegosUsoServiceImpl implements JuegosUsoService {

    private final JuegosUsoRepository juegosUsoRepository;
    private final JuegoRepository juegoRepository;

    // Crea un nuevo registro de uso de juego a partir de un request
    @Override
    @Transactional
    public JuegosUsoResponse create(JuegosUsoRequest request) {
        if (request.idJuego() == null) {
            throw new IllegalArgumentException("El idJuego es obligatorio.");
        }
        if (request.fechaUso() == null) {
            throw new IllegalArgumentException("La fechaUso es obligatoria.");
        }

        Juego juego = juegoRepository.findById(request.idJuego())
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado: " + request.idJuego()));

        JuegosUso ju = new JuegosUso();
        ju.setJuego(juego);
        ju.setCantidadUso(request.cantidadUso());
        ju.setFechaUso(request.fechaUso());
        ju.setDescripcion(request.descripcion());

        return mapToResponse(juegosUsoRepository.save(ju));
    }

    // Busca un registro de uso por su id
    @Override
    @Transactional(readOnly = true)
    public JuegosUsoResponse findById(Integer id) {
        JuegosUso ju = juegosUsoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de uso no encontrado: " + id));
        return mapToResponse(ju);
    }

    // Busca todos los registros de uso
    @Override
    @Transactional(readOnly = true)
    public List<JuegosUsoResponse> findAll() {
        return juegosUsoRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Busca registros de uso por id de juego
    @Override
    @Transactional(readOnly = true)
    public List<JuegosUsoResponse> findByGame(Integer idJuego) {
        if (idJuego == null) {
            throw new IllegalArgumentException("El idJuego es obligatorio.");
        }
        return juegosUsoRepository.findByJuego_IdJuego(idJuego).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Busca registros de uso por fecha de uso
    @Override
    @Transactional(readOnly = true)
    public List<JuegosUsoResponse> findByDate(LocalDate fechaUso) {
        if (fechaUso == null) {
            throw new IllegalArgumentException("La fechaUso es obligatoria.");
        }
        return juegosUsoRepository.findByFechaUso(fechaUso).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Busca registros de uso por id de juego y fecha de uso
    @Override
    @Transactional(readOnly = true)
    public List<JuegosUsoResponse> findByGameAndDate(Integer idJuego, LocalDate fechaUso) {
        if (idJuego == null) {
            throw new IllegalArgumentException("El idJuego es obligatorio.");
        }
        if (fechaUso == null) {
            throw new IllegalArgumentException("La fechaUso es obligatoria.");
        }

        return juegosUsoRepository.findByJuego_IdJuegoAndFechaUso(idJuego, fechaUso).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Actualiza un registro de uso existente
    @Override
    @Transactional
    public JuegosUsoResponse update(Integer id, JuegosUsoRequest request) {
        JuegosUso ju = juegosUsoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de uso no encontrado: " + id));

        if (request.idJuego() != null) {
            Juego juego = juegoRepository.findById(request.idJuego())
                    .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado: " + request.idJuego()));
            ju.setJuego(juego);
        }

        if (request.cantidadUso() != null) {
            ju.setCantidadUso(request.cantidadUso());
        }

        if (request.fechaUso() != null) {
            ju.setFechaUso(request.fechaUso());
        }

        if (request.descripcion() != null) {
            ju.setDescripcion(request.descripcion());
        }

        return mapToResponse(juegosUsoRepository.save(ju));
    }

    // Elimina un registro de uso por su id
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!juegosUsoRepository.existsById(id)) {
            throw new IllegalArgumentException("Registro de uso no encontrado: " + id);
        }
        juegosUsoRepository.deleteById(id);
    }

    // ---------------- helpers ----------------
    // Mapea una entidad JuegosUso a un DTO de respuesta
    private JuegosUsoResponse mapToResponse(JuegosUso ju) {
        Juego j = ju.getJuego();
        JuegoSummary juegoSummary = new JuegoSummary(
                j.getIdJuego(),
                j.getCodigo(),
                j.getNombre(),
                j.getEstado(),
                j.getProxMant(),
                j.getActivo()
        );

        return new JuegosUsoResponse(
                ju.getIdJuegosUso(),
                juegoSummary,
                ju.getCantidadUso(),
                ju.getFechaUso(),
                ju.getDescripcion()
        );
    }
}