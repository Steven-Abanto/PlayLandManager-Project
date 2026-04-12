package com.playlandpark.authservice.auth.service.impl;

import com.playlandpark.authservice.auth.dto.registro.ClienteRegistroRequest;
import com.playlandpark.authservice.auth.dto.registro.EmpleadoRegistroRequest;
import com.playlandpark.authservice.auth.dto.usuario.UsuarioRequest;
import com.playlandpark.authservice.auth.dto.usuario.UsuarioResponse;
import com.playlandpark.authservice.auth.entity.Usuario;
import com.playlandpark.authservice.auth.enums.RolesUsuario;
import com.playlandpark.authservice.auth.repository.UsuarioRepository;
import com.playlandpark.authservice.auth.service.UsuarioService;
import com.playlandpark.authservice.integration.core.CoreConsultaService;
import com.playlandpark.authservice.integration.core.dto.ClienteCoreRequest;
import com.playlandpark.authservice.integration.core.dto.EmpleadoCoreRequest;
import com.playlandpark.authservice.integration.keycloak.KeycloakAdminService;
import com.playlandpark.authservice.integration.keycloak.dto.KeycloakUserRequest;
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
    private final KeycloakAdminService keycloakAdminService;

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

    @Override
    @Transactional
    public UsuarioResponse registrarClienteCompleto(ClienteRegistroRequest request) {
        ClienteCoreRequest clienteCoreRequest = new ClienteCoreRequest(
                request.tipoDoc(),
                request.numeDoc(),
                request.nombre(),
                request.apePaterno(),
                request.apeMaterno(),
                request.genero(),
                request.fechaNac(),
                request.correo(),
                request.telefono(),
                request.direccion(),
                true
        );

        var clienteCreado = coreConsultaService.crearCliente(clienteCoreRequest);

        String username = request.cuenta().usuario();

        try {
            keycloakAdminService.createUser(new KeycloakUserRequest(
                    username,
                    request.cuenta().contrasena(),
                    request.correo(),
                    request.nombre(),
                    buildLastName(request.apePaterno(), request.apeMaterno()),
                    "CLIENTE"
            ));

            UsuarioRequest usuarioRequest = new UsuarioRequest(
                    username,
                    request.cuenta().contrasena(),
                    RolesUsuario.CLIENTE,
                    null,
                    clienteCreado.idCliente(),
                    true
            );

            return create(usuarioRequest);
        } catch (Exception e) {
            keycloakAdminService.deleteUserIfExists(username);
            throw e;
        }
    }

    @Override
    @Transactional
    public UsuarioResponse registrarEmpleadoCompleto(EmpleadoRegistroRequest request) {
        EmpleadoCoreRequest empleadoCoreRequest = new EmpleadoCoreRequest(
                request.tipoDoc(),
                request.numeDoc(),
                request.nombre(),
                request.apePaterno(),
                request.apeMaterno(),
                request.genero(),
                request.fechaNac(),
                request.correo(),
                request.telefono(),
                request.direccion(),
                request.local(),
                request.idCargo(),
                request.fechaInicio(),
                request.fechaFin(),
                true
        );

        var empleadoCreado = coreConsultaService.crearEmpleado(empleadoCoreRequest);

        String username = request.cuenta().usuario();

        try {
            keycloakAdminService.createUser(new KeycloakUserRequest(
                    username,
                    request.cuenta().contrasena(),
                    request.correo(),
                    request.nombre(),
                    buildLastName(request.apePaterno(), request.apeMaterno()),
                    "EMPLEADO"
            ));

            UsuarioRequest usuarioRequest = new UsuarioRequest(
                    username,
                    request.cuenta().contrasena(),
                    RolesUsuario.EMPLEADO,
                    empleadoCreado.idEmpleado(),
                    null,
                    true
            );

            return create(usuarioRequest);
        } catch (Exception e) {
            keycloakAdminService.deleteUserIfExists(username);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findById(Integer id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        return mapToResponse(u);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findByUsuario(String usuario) {
        Usuario u = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuario));
        return mapToResponse(u);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> findByRol(String rol, boolean onlyActive) {
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("Rol no puede ser nulo o vacío.");
        }

        RolesUsuario rolEnum;
        try {
            rolEnum = RolesUsuario.valueOf(rol.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Rol no válido: " + rol);
        }

        List<Usuario> list = onlyActive
                ? usuarioRepository.findByRolAndActivoTrue(rolEnum)
                : usuarioRepository.findByRol(rolEnum);

        return list.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

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

    @Override
    @Transactional
    public void logicDelete(Integer id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        u.setActivo(false);
        usuarioRepository.save(u);
    }

    private void validarRelacion(UsuarioRequest request) {
        Integer idEmpleado = request.idEmpleado();
        Integer idCliente = request.idCliente();

        if (idEmpleado != null && idCliente != null) {
            throw new IllegalArgumentException("Solo se permite idEmpleado o idCliente, no ambos.");
        }

        if (request.rol() == RolesUsuario.CLIENTE && idCliente == null) {
            throw new IllegalArgumentException("Para rol CLIENTE se requiere idCliente.");
        }
        if (request.rol() == RolesUsuario.EMPLEADO && idEmpleado == null) {
            throw new IllegalArgumentException("Para rol EMPLEADO se requiere idEmpleado.");
        }
        if (request.rol() == RolesUsuario.ADMIN && idEmpleado == null) {
            throw new IllegalArgumentException("Para rol ADMIN se requiere idEmpleado.");
        }

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

    private String buildLastName(String apePaterno, String apeMaterno) {
        String paterno = apePaterno != null ? apePaterno.trim() : "";
        String materno = apeMaterno != null ? apeMaterno.trim() : "";
        return (paterno + " " + materno).trim();
    }

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