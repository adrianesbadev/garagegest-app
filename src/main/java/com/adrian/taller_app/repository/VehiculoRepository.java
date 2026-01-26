package com.adrian.taller_app.repository;

import com.adrian.taller_app.domain.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    @EntityGraph(attributePaths = "cliente")
    List<Vehiculo> findAllByOrderByMatriculaAsc();

    @EntityGraph(attributePaths = "cliente")
    Page<Vehiculo> findAllByOrderByMatriculaAsc(Pageable pageable);

    @EntityGraph(attributePaths = "cliente")
    Optional<Vehiculo> findWithClienteByIdVehiculo(Long idVehiculo);

    @EntityGraph(attributePaths = "cliente")
    List<Vehiculo> findAllByCliente_IdClienteOrderByMatriculaAsc(Long idCliente);

    boolean existsByCliente_IdCliente(Long idCliente);

    boolean existsByMatriculaIgnoreCase(String matricula);

    Optional<Vehiculo> findByMatriculaIgnoreCase(String matricula);
}
