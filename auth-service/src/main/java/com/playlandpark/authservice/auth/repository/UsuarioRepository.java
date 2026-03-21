package com.playlandpark.authservice.auth.repository;

import com.playlandpark.authservice.auth.entity.Usuario;
import com.playlandpark.authservice.auth.enums.RolesUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsuario(String usuario);

    Optional<Usuario> findByIdEmpleado(Integer idEmpleado);

    Optional<Usuario> findByIdCliente(Integer idCliente);

    List<Usuario> findByRol(RolesUsuario rol);

    List<Usuario> findByRolAndActivoTrue(RolesUsuario rol);

    boolean existsByUsuario(String usuario);
}