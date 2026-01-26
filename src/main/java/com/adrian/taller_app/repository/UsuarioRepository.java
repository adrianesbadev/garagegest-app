package com.adrian.taller_app.repository;

import com.adrian.taller_app.domain.RolUsuario;
import com.adrian.taller_app.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findAllByActivoTrueOrderByNombreAsc();

    List<Usuario> findAllByActivoTrueAndRolOrderByNombreAsc(RolUsuario rol);

    Page<Usuario> findAllByOrderByNombreAsc(Pageable pageable);

    Optional<Usuario> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
