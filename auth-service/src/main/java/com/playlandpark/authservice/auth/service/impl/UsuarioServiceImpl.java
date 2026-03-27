package com.playlandpark.authservice.auth.service.impl;

import com.playlandpark.authservice.auth.dto.usuario.UsuarioRequest;
import com.playlandpark.authservice.auth.dto.usuario.UsuarioResponse;
import com.playlandpark.authservice.auth.entity.Usuario;
import com.playlandpark.authservice.auth.enums.RolesUsuario;
import com.playlandpark.authservice.auth.repository.UsuarioRepository;
import com.playlandpark.authservice.auth.service.UsuarioService;
import com.playlandpark.authservice.integration.core.CoreConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CoreConsultaService coreConsultaService;

    // Crea un nuevo usuario
    @Override
    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        validarRelacion(request);

        if (usuarioRepository.existsByUsuario(request.usuario())) {
            throw new IllegalArgumentException("El usuario ya existe: " + request.usuario());
        }

        validarUnicidadRelacion(request.idEmpleado(), request.idCliente(), null);

        Usuario u = new Usuario();
        u.setUsuario(request.usuario());
        u.setContrasena(passwordEncoder.encode(request.contrasena()));
        u.setRol(request.rol());
        u.setActivo(request.activo() != null ? request.activo() : true);
        u.setIdEmpleado(request.idEmpleado());
        u.setIdCliente(request.idCliente());

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

        if (request.usuario() != null && !request.usuario().equals(u.getUsuario())) {
            if (usuarioRepository.existsByUsuario(request.usuario())) {
                throw new IllegalArgumentException("El usuario ya existe: " + request.usuario());
            }
            u.setUsuario(request.usuario());
        }

        if (request.contrasena() != null && !request.contrasena().isBlank()) {
// Se añade hasheo
//            u.setContrasena(request.contrasena());
            u.setContrasena(passwordEncoder.encode(request.contrasena()));
        }

        if (request.rol() != null) {
            u.setRol(request.rol());
        }

        if (request.activo() != null) {
            u.setActivo(request.activo());
        }

        boolean actualizaRelacion = request.idEmpleado() != null || request.idCliente() != null;
        if (actualizaRelacion) {
            validarUnicidadRelacion(request.idEmpleado(), request.idCliente(), u.getIdUsuario());
            u.setIdEmpleado(request.idEmpleado());
            u.setIdCliente(request.idCliente());
        }

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

        // Validar existencia en core-service
        if (idCliente != null) {
            coreConsultaService.obtenerCliente(idCliente);
        }

        if (idEmpleado != null) {
            coreConsultaService.obtenerEmpleado(idEmpleado);
        }
    }

    private void validarUnicidadRelacion(Integer idEmpleado, Integer idCliente, Integer idUsuarioActual) {
        if (idEmpleado != null) {
            var existente = usuarioRepository.findByIdEmpleado(idEmpleado);
            if (existente.isPresent() && !existente.get().getIdUsuario().equals(idUsuarioActual)) {
                throw new IllegalArgumentException("Ese empleado ya tiene un usuario asignado.");
            }
        }

        if (idCliente != null) {
            var existente = usuarioRepository.findByIdCliente(idCliente);
            if (existente.isPresent() && !existente.get().getIdUsuario().equals(idUsuarioActual)) {
                throw new IllegalArgumentException("Ese cliente ya tiene un usuario asignado.");
            }
        }
    }

// Se comenta porque ya no hay relaciones JPA
//    // Asigna o actualiza la relación entre usuario-empleado-cliente
//    private void asignarRelacion(Usuario u, UsuarioRequest request, boolean esCreacion) {
//        Integer idEmpleado = request.idEmpleado();
//        Integer idCliente = request.idCliente();
//
//        // Si no mandan IDs, y ya existe relación (update), no la toques.
//        if (!esCreacion && idEmpleado == null && idCliente == null) return;
//
//        // Reset relaciones
//        u.setEmpleado(null);
//        u.setCliente(null);
//
//        if (idEmpleado != null) {
//            if (usuarioRepository.existsByEmpleado_IdEmpleado(idEmpleado)
//                    && (esCreacion || !idEmpleado.equals(u.getEmpleado() != null ? u.getEmpleado().getIdEmpleado() : null))) {
//                throw new IllegalArgumentException("Ese empleado ya tiene un usuario asignado.");
//            }
//
//            Empleado e = empleadoRepository.findById(idEmpleado)
//                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado: " + idEmpleado));
//            u.setEmpleado(e);
//        }
//
//        if (idCliente != null) {
//            if (usuarioRepository.existsByCliente_IdCliente(idCliente)
//                    && (esCreacion || !idCliente.equals(u.getCliente() != null ? u.getCliente().getIdCliente() : null))) {
//                throw new IllegalArgumentException("Ese cliente ya tiene un usuario asignado.");
//            }
//
//            Cliente c = clienteRepository.findById(idCliente)
//                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + idCliente));
//            u.setCliente(c);
//        }
//
//        // Validación extra opcional: coherencia rol-relación
//        if (u.getRol() == RolesUsuario.CLIENTE && u.getCliente() == null) {
//            throw new IllegalArgumentException("Rol CLIENTE requiere relación con Cliente.");
//        }
//        if (u.getRol() == RolesUsuario.EMPLEADO && u.getEmpleado() == null) {
//            throw new IllegalArgumentException("Rol EMPLEADO requiere relación con Empleado.");
//        }
//    }

    // Mapea un Usuario a UsuarioResponse
    private UsuarioResponse mapToResponse(Usuario u) {
        return new UsuarioResponse(
                u.getIdUsuario(),
                u.getUsuario(),
                u.getRol(),
                u.getActivo(),
                u.getIdEmpleado(),
                u.getIdCliente()
        );
    }
}