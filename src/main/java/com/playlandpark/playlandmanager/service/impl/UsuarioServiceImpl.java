package com.playlandpark.playlandmanager.service.impl;

import com.playlandpark.playlandmanager.model.dto.summary.ClienteSummary;
import com.playlandpark.playlandmanager.model.dto.summary.EmpleadoSummary;
import com.playlandpark.playlandmanager.model.dto.usuario.UsuarioRequest;
import com.playlandpark.playlandmanager.model.dto.usuario.UsuarioResponse;
import com.playlandpark.playlandmanager.model.entity.Cliente;
import com.playlandpark.playlandmanager.model.entity.Empleado;
import com.playlandpark.playlandmanager.model.entity.Usuario;
import com.playlandpark.playlandmanager.model.enums.RolesUsuario;
import com.playlandpark.playlandmanager.repository.ClienteRepository;
import com.playlandpark.playlandmanager.repository.EmpleadoRepository;
import com.playlandpark.playlandmanager.repository.UsuarioRepository;
import com.playlandpark.playlandmanager.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ClienteRepository clienteRepository;

    // Crea un nuevo usuario
    @Override
    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        validarRelacion(request);

        if (usuarioRepository.existsByUsuario(request.usuario())) {
            throw new IllegalArgumentException("El usuario ya existe: " + request.usuario());
        }

        Usuario u = new Usuario();
        u.setUsuario(request.usuario());
        u.setContrasena(request.contrasena());
        u.setRol(request.rol());
        u.setActivo(request.activo() != null ? request.activo() : true);

        asignarRelacion(u, request, true);

        Usuario guardado = usuarioRepository.save(u);
        return mapToResponse(guardado);
    }

    // Busca un usuario por su id
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findById(Integer id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        return mapToResponse(u);
    }

    // Busca un usuario por su nombre de usuario
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findByUsuario(String usuario) {
        Usuario u = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuario));
        return mapToResponse(u);
    }

    // Busca usuarios por rol, con opción de filtrar solo los activos
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> findByRol(String rol, boolean onlyActive) {
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("Rol no puede ser nulo o vacío.");
        }

        // Validar que el rol exista en el enum
        RolesUsuario rolEnum;
        try {
            rolEnum = RolesUsuario.valueOf(rol.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Rol no válido: " + rol);
        }

        List<Usuario> list =  onlyActive
                ? usuarioRepository.findByRolAndActivoTrue(rolEnum)
                : usuarioRepository.findByRol(rolEnum);

        return list.stream().map(this::mapToResponse).toList();
    }

    // Busca todos los usuarios, con opción de filtrar solo los activos
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Actualiza un usuario existente
    @Override
    @Transactional
    public UsuarioResponse update(Integer id, UsuarioRequest request) {
        validarRelacion(request);

        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        // usuario único (si cambia)
        if (request.usuario() != null && !request.usuario().equals(u.getUsuario())) {
            if (usuarioRepository.existsByUsuario(request.usuario())) {
                throw new IllegalArgumentException("El usuario ya existe: " + request.usuario());
            }
            u.setUsuario(request.usuario());
        }

        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            u.setContrasena(request.contrasena());
        }

        if (request.rol() != null) {
            u.setRol(request.rol());
        }

        if (request.activo() != null) {
            u.setActivo(request.activo());
        }

        asignarRelacion(u, request, false);

        return mapToResponse(usuarioRepository.save(u));
    }

    // Elimina lógicamente un usuario (activo = false)
    @Override
    @Transactional
    public void logicDelete(Integer id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        u.setActivo(false);
        usuarioRepository.save(u);
    }

    // ----------------- helpers -----------------
    // Valida que la relación entre usuario-empleado-cliente sea coherente con el rol
    private void validarRelacion(UsuarioRequest request) {
        Integer idEmpleado = request.idEmpleado();
        Integer idCliente = request.idCliente();

        // No ambos
        if (idEmpleado != null && idCliente != null) {
            throw new IllegalArgumentException("Solo se permite idEmpleado o idCliente, no ambos.");
        }

        // Según rol
        if (request.rol() == RolesUsuario.CLIENTE && idCliente == null) {
            throw new IllegalArgumentException("Para rol CLIENTE se requiere idCliente.");
        }
        if (request.rol() == RolesUsuario.EMPLEADO && idEmpleado == null) {
            throw new IllegalArgumentException("Para rol EMPLEADO se requiere idEmpleado.");
        }
        if (request.rol() == RolesUsuario.ADMIN && idEmpleado == null) {
            throw new IllegalArgumentException("Para rol ADMIN se requiere idEmpleado.");
        }
    }

    // Asigna o actualiza la relación entre usuario-empleado-cliente
    private void asignarRelacion(Usuario u, UsuarioRequest request, boolean esCreacion) {
        Integer idEmpleado = request.idEmpleado();
        Integer idCliente = request.idCliente();

        // Si no mandan IDs, y ya existe relación (update), no la toques.
        if (!esCreacion && idEmpleado == null && idCliente == null) return;

        // Reset relaciones
        u.setEmpleado(null);
        u.setCliente(null);

        if (idEmpleado != null) {
            if (usuarioRepository.existsByEmpleado_IdEmpleado(idEmpleado)
                    && (esCreacion || !idEmpleado.equals(u.getEmpleado() != null ? u.getEmpleado().getIdEmpleado() : null))) {
                throw new IllegalArgumentException("Ese empleado ya tiene un usuario asignado.");
            }

            Empleado e = empleadoRepository.findById(idEmpleado)
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado: " + idEmpleado));
            u.setEmpleado(e);
        }

        if (idCliente != null) {
            if (usuarioRepository.existsByCliente_IdCliente(idCliente)
                    && (esCreacion || !idCliente.equals(u.getCliente() != null ? u.getCliente().getIdCliente() : null))) {
                throw new IllegalArgumentException("Ese cliente ya tiene un usuario asignado.");
            }

            Cliente c = clienteRepository.findById(idCliente)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + idCliente));
            u.setCliente(c);
        }

        // Validación extra opcional: coherencia rol-relación
        if (u.getRol() == RolesUsuario.CLIENTE && u.getCliente() == null) {
            throw new IllegalArgumentException("Rol CLIENTE requiere relación con Cliente.");
        }
        if (u.getRol() == RolesUsuario.EMPLEADO && u.getEmpleado() == null) {
            throw new IllegalArgumentException("Rol EMPLEADO requiere relación con Empleado.");
        }
    }

    // Mapea un Usuario a UsuarioResponse, incluyendo los resúmenes de Empleado o Cliente si existen
    private UsuarioResponse mapToResponse(Usuario u) {
        EmpleadoSummary empleado = null;
        if (u.getEmpleado() != null) {
            var e = u.getEmpleado();
            empleado = new EmpleadoSummary(
                    e.getIdEmpleado(),
                    e.getTipoDoc(),
                    e.getNumeDoc(),
                    e.getNombre(),
                    e.getApePaterno(),
                    e.getApeMaterno(),
                    e.getCorreo(),
                    e.getCargo().getRol()
            );
        }

        ClienteSummary cliente = null;
        if (u.getCliente() != null) {
            var c = u.getCliente();
            cliente = new ClienteSummary(
                    c.getIdCliente(),
                    c.getTipoDoc(),
                    c.getNumeDoc(),
                    c.getNombre(),
                    c.getApePaterno(),
                    c.getApeMaterno(),
                    c.getCorreo()
            );
        }

        return new UsuarioResponse(
                u.getIdUsuario(),
                u.getUsuario(),
                u.getRol(),
                u.getActivo(),
                empleado,
                cliente
        );
    }
}