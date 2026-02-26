package com.playlandpark.playlandmanager.repository;

import com.playlandpark.playlandmanager.model.entity.Usuario;
import com.playlandpark.playlandmanager.model.enums.RolesUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsuario(String usuario);

    List<Usuario> findByRol(RolesUsuario rol);

    List<Usuario> findByRolAndActivoTrue(RolesUsuario rol);

    boolean existsByUsuario(String usuario);

    boolean existsByEmpleado_IdEmpleado(Integer idEmpleado);

    boolean existsByCliente_IdCliente(Integer idCliente);
}