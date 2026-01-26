package com.adrian.taller_app.service;

import com.adrian.taller_app.domain.RolUsuario;
import com.adrian.taller_app.domain.Usuario;
import com.adrian.taller_app.repository.OrdenTrabajoRepository;
import com.adrian.taller_app.repository.UsuarioRepository;
import com.adrian.taller_app.web.UsuarioForm;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de usuarios del sistema.
 * Maneja operaciones CRUD y validaciones de usuarios.
 */
@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          OrdenTrabajoRepository ordenTrabajoRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> findActivos() {
        return usuarioRepository.findAllByActivoTrueOrderByNombreAsc();
    }

    public List<Usuario> findMecanicosActivos() {
        return usuarioRepository.findAllByActivoTrueAndRolOrderByNombreAsc(RolUsuario.MECANICO);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepository.findAllByOrderByNombreAsc(pageable);
    }

    public Usuario findById(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    /**
     * Verifica si existe un usuario con el username especificado.
     *
     * @param username nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return usuarioRepository.existsByUsernameIgnoreCase(username.trim());
    }

    /**
     * Verifica si existe un usuario con el username especificado, excluyendo el usuario con el ID dado.
     *
     * @param username nombre de usuario a verificar
     * @param idUsuario ID del usuario a excluir de la búsqueda
     * @return true si existe otro usuario con ese username, false en caso contrario
     */
    public boolean existsByUsernameExcludingId(String username, Long idUsuario) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return usuarioRepository.findByUsernameIgnoreCase(username.trim())
                .filter(usuario -> !usuario.getIdUsuario().equals(idUsuario))
                .isPresent();
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param form formulario con los datos del usuario
     * @return el usuario creado
     * @throws IllegalStateException si el username ya existe o la contraseña está vacía
     */
    @Transactional
    public Usuario create(UsuarioForm form) {
        if (usuarioRepository.existsByUsernameIgnoreCase(form.getUsername())) {
            throw new IllegalStateException("El nombre de usuario ya existe.");
        }
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            throw new IllegalStateException("La contraseña es obligatoria.");
        }
        Usuario usuario = new Usuario();
        applyForm(usuario, form);
        usuario.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param idUsuario ID del usuario a actualizar
     * @param form formulario con los nuevos datos
     * @return el usuario actualizado
     * @throws EntityNotFoundException si el usuario no existe
     * @throws IllegalStateException si el username ya está en uso por otro usuario
     */
    @Transactional
    public Usuario update(Long idUsuario, UsuarioForm form) {
        Usuario usuario = findById(idUsuario);
        usuarioRepository.findByUsernameIgnoreCase(form.getUsername())
                .filter(existente -> !existente.getIdUsuario().equals(idUsuario))
                .ifPresent(existente -> {
                    throw new IllegalStateException("El nombre de usuario ya existe.");
                });
        applyForm(usuario, form);
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }
        return usuario;
    }

    @Transactional
    public void delete(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        if (ordenTrabajoRepository.existsByUsuarioAsignado_IdUsuario(idUsuario)) {
            throw new IllegalStateException("No se puede eliminar el usuario porque tiene OT asignadas.");
        }
        usuarioRepository.deleteById(idUsuario);
    }

    private void applyForm(Usuario usuario, UsuarioForm form) {
        usuario.setUsername(clean(form.getUsername()));
        usuario.setNombre(clean(form.getNombre()));
        usuario.setEmail(clean(form.getEmail()));
        usuario.setRol(form.getRol());
        usuario.setActivo(form.isActivo());
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
