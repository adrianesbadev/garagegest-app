package com.adrian.taller_app.repository;

import com.adrian.taller_app.domain.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @EntityGraph(attributePaths = "vehiculos")
    Optional<Cliente> findWithVehiculosByIdCliente(Long idCliente);

    boolean existsByNifIgnoreCase(String nif);

    Optional<Cliente> findByNifIgnoreCase(String nif);

    Page<Cliente> findAllByOrderByNombreAsc(Pageable pageable);
}
