package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.mantenimiento.MantenimientoRequest;
import com.playlandpark.playlandmanager.model.dto.mantenimiento.MantenimientoResponse;
import com.playlandpark.playlandmanager.model.dto.summary.JuegoSummary;
import com.playlandpark.playlandmanager.model.entity.Empleado;
import com.playlandpark.playlandmanager.model.entity.Juego;
import com.playlandpark.playlandmanager.model.entity.Mantenimiento;
import com.playlandpark.playlandmanager.repository.EmpleadoRepository;
import com.playlandpark.playlandmanager.repository.JuegoRepository;
import com.playlandpark.playlandmanager.repository.MantenimientoRepository;
import com.playlandpark.playlandmanager.service.MantenimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MantenimientoServiceImpl implements MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final JuegoRepository juegoRepository;

    // Crea un nuevo mantenimiento a partir de un request
    @Override
    @Transactional
    public MantenimientoResponse create(MantenimientoRequest request) {
        validateRequired(request);

        if (request.fechaFin().isBefore(request.fechaInicio())) {
            throw new IllegalArgumentException("La fechaFin no puede ser menor que la fechaInicio.");
        }

        Empleado empleado = empleadoRepository.findById(request.idEmpleado())
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado: " + request.idEmpleado()));

        Juego juego = juegoRepository.findById(request.idJuego())
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado: " + request.idJuego()));

        Mantenimiento m = new Mantenimiento();
        m.setEmpleado(empleado);
        m.setJuego(juego);
        m.setFechaInicio(request.fechaInicio());
        m.setFechaFin(request.fechaFin());
        m.setResultado(request.resultado());
        m.setObservaciones(request.observaciones());

        return mapToResponse(mantenimientoRepository.save(m));
    }

    // Busca un mantenimiento por su id
    @Override
    @Transactional(readOnly = true)
    public MantenimientoResponse findById(Integer id) {
        Mantenimiento m = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));
        return mapToResponse(m);
    }

    // Busca todos los mantenimientos
    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoResponse> findAll() {
        return mantenimientoRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Busca mantenimientos por id de empleado
    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoResponse> findByEmployee(Integer idEmpleado) {
        if (idEmpleado == null) {
            throw new IllegalArgumentException("El idEmpleado es obligatorio.");
        }
        return mantenimientoRepository.findByEmpleado_IdEmpleado(idEmpleado).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Busca mantenimientos por id de juego
    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoResponse> findByGame(Integer idJuego) {
        if (idJuego == null) {
            throw new IllegalArgumentException("El idJuego es obligatorio.");
        }
        return mantenimientoRepository.findByJuego_IdJuego(idJuego).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Busca mantenimientos por rango de fecha de inicio
    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoResponse> findByStartDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("La fecha fin no puede ser menor que la fecha inicio.");
        }

        return mantenimientoRepository.findByFechaInicioBetween(start, end).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Busca mantenimientos por rango de fecha de fin
    @Override
    @Transactional
    public MantenimientoResponse update(Integer id, MantenimientoRequest request) {
        Mantenimiento m = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado: " + id));

        if (request.idEmpleado() != null) {
            Empleado empleado = empleadoRepository.findById(request.idEmpleado())
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado: " + request.idEmpleado()));
            m.setEmpleado(empleado);
        }

        if (request.idJuego() != null) {
            Juego juego = juegoRepository.findById(request.idJuego())
                    .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado: " + request.idJuego()));
            m.setJuego(juego);
        }

        LocalDate newInicio = request.fechaInicio() != null ? request.fechaInicio() : m.getFechaInicio();
        LocalDate newFin = request.fechaFin() != null ? request.fechaFin() : m.getFechaFin();

        if (newFin.isBefore(newInicio)) {
            throw new IllegalArgumentException("La fechaFin no puede ser menor que la fechaInicio.");
        }

        if (request.fechaInicio() != null) m.setFechaInicio(request.fechaInicio());
        if (request.fechaFin() != null) m.setFechaFin(request.fechaFin());
        if (request.resultado() != null) m.setResultado(request.resultado());
        if (request.observaciones() != null) m.setObservaciones(request.observaciones());

        return mapToResponse(mantenimientoRepository.save(m));
    }

    // Elimina un mantenimiento por su id
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!mantenimientoRepository.existsById(id)) {
            throw new IllegalArgumentException("Mantenimiento no encontrado: " + id);
        }
        mantenimientoRepository.deleteById(id);
    }

    // ---------------- helpers ----------------
    // Valida los datos requeridos para crear un mantenimiento
    private void validateRequired(MantenimientoRequest request) {
        if (request.idEmpleado() == null) throw new IllegalArgumentException("El idEmpleado es obligatorio.");
        if (request.idJuego() == null) throw new IllegalArgumentException("El idJuego es obligatorio.");
        if (request.fechaInicio() == null) throw new IllegalArgumentException("La fechaInicio es obligatoria.");
        if (request.fechaFin() == null) throw new IllegalArgumentException("La fechaFin es obligatoria.");
        if (request.resultado() == null || request.resultado().isBlank())
            throw new IllegalArgumentException("El resultado es obligatorio.");
    }

    // Mapea un mantenimiento a su respuesta DTO
    private MantenimientoResponse mapToResponse(Mantenimiento m) {
        var e = m.getEmpleado();
        String empleadoNombre = (e.getNombre() + " " + e.getApePaterno() + " " + e.getApeMaterno()).trim();

        var j = m.getJuego();
        JuegoSummary juegoSummary = new JuegoSummary(
                j.getIdJuego(),
                j.getCodigo(),
                j.getNombre(),
                j.getEstado(),
                j.getProxMant(),
                j.getActivo()
        );

        return new MantenimientoResponse(
                m.getIdMantenimiento(),
                e.getIdEmpleado(),
                empleadoNombre,
                juegoSummary,
                m.getFechaInicio(),
                m.getFechaFin(),
                m.getResultado(),
                m.getObservaciones()
        );
    }
}