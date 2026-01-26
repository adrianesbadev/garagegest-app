package com.adrian.taller_app.security;

import com.adrian.taller_app.domain.RolUsuario;
import com.adrian.taller_app.domain.Usuario;
import com.adrian.taller_app.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        RolUsuario rol = usuario.getRol();
        if (rol == null) {
            throw new UsernameNotFoundException("El usuario no tiene rol asignado");
        }
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .roles(rol.name())
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}
