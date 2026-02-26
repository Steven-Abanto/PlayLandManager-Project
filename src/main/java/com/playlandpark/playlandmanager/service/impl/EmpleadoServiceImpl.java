package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.cargo.CargoResponse;
import com.playlandpark.playlandmanager.model.dto.empleado.EmpleadoRequest;
import com.playlandpark.playlandmanager.model.dto.empleado.EmpleadoResponse;
import com.playlandpark.playlandmanager.model.entity.Cargo;
import com.playlandpark.playlandmanager.model.entity.Empleado;
import com.playlandpark.playlandmanager.repository.CargoRepository;
import com.playlandpark.playlandmanager.repository.EmpleadoRepository;
import com.playlandpark.playlandmanager.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final CargoRepository cargoRepository;

    // Crea un nuevo empleado
    @Override
    public EmpleadoResponse create(EmpleadoRequest request) {
        validateRequiredForCreate(request);

        String numeDoc = request.numeDoc().trim();
        String correoNorm = normalizeEmail(request.correo());
        String telefono = request.telefono().trim();

        if (empleadoRepository.existsByNumeDoc(numeDoc)) {
            throw new IllegalArgumentException("El número de documento ya está registrado: " + numeDoc);
        }
        if (empleadoRepository.existsByCorreo(correoNorm)) {
            throw new IllegalArgumentException("El correo ya está registrado: " + request.correo());
        }
        if (empleadoRepository.existsByTelefono(telefono)) {
            throw new IllegalArgumentException("El teléfono ya está registrado: " + request.telefono());
        }

        Cargo cargo = getCargo(request.idCargo());

        Empleado empleado = new Empleado();
        applyRequestToEntity(empleado, request, cargo);
        if (empleado.getActivo() == null) empleado.setActivo(true);
        empleado.setNumeDoc(numeDoc);
        empleado.setCorreo(correoNorm);
        empleado.setTelefono(telefono);

        Empleado saved = empleadoRepository.save(empleado);
        return mapToResponse(saved);
    }

    // Busca un empleado por su id
    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponse findById(Integer idEmpleado) {
        Empleado empleado = getEmpleado(idEmpleado);
        return mapToResponse(empleado);
    }

    // Busca un empleado por su número de documento
    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponse findByDocument(String numeDoc) {
        if (numeDoc == null || numeDoc.isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio.");
        }

        Empleado empleado = empleadoRepository.findByNumeDoc(numeDoc.trim())
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con documento: " + numeDoc));

        return mapToResponse(empleado);
    }

    // Busca un empleado por su correo
    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponse> findAll(boolean onlyActive) {
        List<Empleado> list = onlyActive
                ? empleadoRepository.findByActivoTrue()
                : empleadoRepository.findAll();

        return list.stream().map(this::mapToResponse).toList();
    }

    // Busca empleados por local
    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponse> findByLocal(Integer local, boolean onlyActive) {
        if (local == null) {
            throw new IllegalArgumentException("El local es obligatorio.");
        }

        List<Empleado> list = onlyActive
                ? empleadoRepository.findByLocalAndActivoTrue(local)
                : empleadoRepository.findByLocal(local);

        return list.stream().map(this::mapToResponse).toList();
    }

    // Busca empleados por cargo
    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponse> findByCargo(Integer idCargo, boolean onlyActive) {
        if (idCargo == null) {
            throw new IllegalArgumentException("El ID de cargo es obligatorio.");
        }

        List<Empleado> list = onlyActive
                ? empleadoRepository.findByCargo_IdCargoAndActivoTrue(idCargo)
                : empleadoRepository.findByCargo_IdCargo(idCargo);

        return list.stream().map(this::mapToResponse).toList();
    }

    // Actualiza un empleado existente
    @Override
    public EmpleadoResponse update(Integer idEmpleado, EmpleadoRequest request) {
        if (request == null) throw new IllegalArgumentException("El request no puede ser nulo.");

        Empleado empleado = getEmpleado(idEmpleado);

        // Unicidad si se cambia
        if (request.numeDoc() != null && !request.numeDoc().isBlank()) {
            String nuevoDoc = request.numeDoc().trim();
            if (!nuevoDoc.equals(empleado.getNumeDoc()) && empleadoRepository.existsByNumeDoc(nuevoDoc)) {
                throw new IllegalArgumentException("El número de documento ya está registrado: " + nuevoDoc);
            }
            empleado.setNumeDoc(nuevoDoc);
        }

        // Unicidad si se cambia
        if (request.correo() != null && !request.correo().isBlank()) {
            String nuevoCorreo = normalizeEmail(request.correo());
            if (!nuevoCorreo.equalsIgnoreCase(empleado.getCorreo()) && empleadoRepository.existsByCorreo(nuevoCorreo)) {
                throw new IllegalArgumentException("El correo ya está registrado: " + request.correo());
            }
            empleado.setCorreo(nuevoCorreo);
        }

        // Unicidad si se cambia
        if (request.telefono() != null && !request.telefono().isBlank()) {
            String nuevoTelefono = request.telefono().trim();
            if (!nuevoTelefono.equals(empleado.getTelefono()) && empleadoRepository.existsByTelefono(nuevoTelefono)) {
                throw new IllegalArgumentException("El teléfono ya está registrado: " + request.telefono());
            }
            empleado.setTelefono(nuevoTelefono);
        }

        // Fechas: si ambas presentes, validar; si solo una cambia, validar contra la otra.
        if (request.fechaInicio() != null) {
            empleado.setFechaInicio(request.fechaInicio());
        }
        if (request.fechaFin() != null) {
            empleado.setFechaFin(request.fechaFin());
        }
        if (empleado.getFechaFin() != null && empleado.getFechaFin().isBefore(empleado.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha fin no puede ser menor que la fecha inicio.");
        }

        // Cargo
        if (request.idCargo() != null) {
            Cargo cargo = getCargo(request.idCargo());
            empleado.setCargo(cargo);
        }

        // Resto de campos
        if (request.tipoDoc() != null) empleado.setTipoDoc(request.tipoDoc());
        if (request.nombre() != null) empleado.setNombre(request.nombre());
        if (request.apePaterno() != null) empleado.setApePaterno(request.apePaterno());
        if (request.apeMaterno() != null) empleado.setApeMaterno(request.apeMaterno());
        if (request.genero() != null) empleado.setGenero(request.genero());
        if (request.fechaNac() != null) empleado.setFechaNac(request.fechaNac());
        if (request.direccion() != null) empleado.setDireccion(request.direccion());
        if (request.local() != null) empleado.setLocal(request.local());
        if (request.activo() != null) empleado.setActivo(request.activo());

        Empleado updated = empleadoRepository.save(empleado);
        return mapToResponse(updated);
    }

    // Eliminación lógica de un empleado (activo=false)
    @Override
    public void logicDelete(Integer idEmpleado) {
        Empleado empleado = getEmpleado(idEmpleado);
        empleado.setActivo(false);
        empleadoRepository.save(empleado);
    }

    // -------------------- helpers --------------------
    // Valida los campos obligatorios para la creación de un empleado
    private void validateRequiredForCreate(EmpleadoRequest request) {
        if (request == null) throw new IllegalArgumentException("El request no puede ser nulo.");

        if (request.tipoDoc() == null || request.tipoDoc().isBlank())
            throw new IllegalArgumentException("El tipo de documento es obligatorio.");

        if (request.numeDoc() == null || request.numeDoc().isBlank())
            throw new IllegalArgumentException("El número de documento es obligatorio.");

        if (request.nombre() == null || request.nombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");

        if (request.apePaterno() == null || request.apePaterno().isBlank())
            throw new IllegalArgumentException("El apellido paterno es obligatorio.");

        if (request.apeMaterno() == null || request.apeMaterno().isBlank())
            throw new IllegalArgumentException("El apellido materno es obligatorio.");

        if (request.genero() == null || request.genero().isBlank())
            throw new IllegalArgumentException("El género es obligatorio.");

        if (request.fechaNac() == null)
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");

        if (request.correo() == null || request.correo().isBlank())
            throw new IllegalArgumentException("El correo es obligatorio.");

        if (request.telefono() == null || request.telefono().isBlank())
            throw new IllegalArgumentException("El teléfono es obligatorio.");

        if (request.direccion() == null || request.direccion().isBlank())
            throw new IllegalArgumentException("La dirección es obligatoria.");

        if (request.local() == null)
            throw new IllegalArgumentException("El local es obligatorio.");

        if (request.idCargo() == null)
            throw new IllegalArgumentException("El cargo es obligatorio.");

        if (request.fechaInicio() == null)
            throw new IllegalArgumentException("La fecha de inicio es obligatoria.");

        if (request.fechaFin() != null && request.fechaFin().isBefore(request.fechaInicio()))
            throw new IllegalArgumentException("La fecha fin no puede ser menor que la fecha inicio.");
    }

    private String normalizeEmail(String correo) {
        return correo.trim().toLowerCase();
    }

    // Obtiene un cargo por su id
    private Cargo getCargo(Integer idCargo) {
        return cargoRepository.findById(idCargo)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado con id: " + idCargo));
    }

    // Obtiene un empleado por su id
    private Empleado getEmpleado(Integer idEmpleado) {
        return empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con id: " + idEmpleado));
    }

    // Aplica los datos del request a la entidad, incluyendo la relación con cargo
    private void applyRequestToEntity(Empleado empleado, EmpleadoRequest request, Cargo cargo) {
        empleado.setTipoDoc(request.tipoDoc());
        empleado.setNumeDoc(request.numeDoc());
        empleado.setNombre(request.nombre());
        empleado.setApePaterno(request.apePaterno());
        empleado.setApeMaterno(request.apeMaterno());
        empleado.setGenero(request.genero());
        empleado.setFechaNac(request.fechaNac());
        empleado.setCorreo(request.correo());
        empleado.setTelefono(request.telefono());
        empleado.setDireccion(request.direccion());
        empleado.setLocal(request.local());
        empleado.setCargo(cargo);
        empleado.setFechaInicio(request.fechaInicio());
        empleado.setFechaFin(request.fechaFin());
        empleado.setActivo(request.activo());
    }

    // Mapea una entidad Empleado a un DTO EmpleadoResponse
    private EmpleadoResponse mapToResponse(Empleado e) {
        return new EmpleadoResponse(
                e.getIdEmpleado(),
                e.getTipoDoc(),
                e.getNumeDoc(),
                e.getNombre(),
                e.getApePaterno(),
                e.getApeMaterno(),
                e.getGenero(),
                e.getFechaNac(),
                e.getCorreo(),
                e.getTelefono(),
                e.getDireccion(),
                e.getLocal(),
                e.getCargo().getRol(),
                e.getFechaInicio(),
                e.getFechaFin(),
                e.getActivo()
        );
    }
}