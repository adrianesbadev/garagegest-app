package com.adrian.taller_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/login", "/error", "/error/**").permitAll()
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/clientes/**", "/vehiculos/**", "/recordatorios/**")
                        .hasAnyRole("ADMIN", "RECEPCION")
                        .requestMatchers(HttpMethod.POST, "/ordenes-trabajo/*/eliminar")
                        .hasAnyRole("ADMIN", "RECEPCION")
                        .requestMatchers(HttpMethod.GET, "/ordenes-trabajo/*/factura")
                        .hasAnyRole("ADMIN", "RECEPCION")
                        .requestMatchers(HttpMethod.GET, "/ordenes-trabajo/exportar")
                        .hasAnyRole("ADMIN", "RECEPCION")
                        .requestMatchers("/ordenes-trabajo/**")
                        .hasAnyRole("ADMIN", "RECEPCION", "MECANICO")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
