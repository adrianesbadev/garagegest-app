package com.adrian.taller_app.config;

import com.adrian.taller_app.domain.RolUsuario;
import com.adrian.taller_app.domain.Usuario;
import com.adrian.taller_app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapAdminRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final boolean force;
    private final String username;
    private final String password;
    private final String nombre;
    private final String email;

    public BootstrapAdminRunner(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.bootstrap-admin.enabled:false}") boolean enabled,
                                @Value("${app.bootstrap-admin.force:false}") boolean force,
                                @Value("${app.bootstrap-admin.username:}") String username,
                                @Value("${app.bootstrap-admin.password:}") String password,
                                @Value("${app.bootstrap-admin.nombre:Administrador}") String nombre,
                                @Value("${app.bootstrap-admin.email:admin@local}") String email) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.force = force;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.email = email;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return;
        }
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (usuario != null && !force) {
            return;
        }
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setUsername(username.trim());
        }
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setRol(RolUsuario.ADMIN);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
}
