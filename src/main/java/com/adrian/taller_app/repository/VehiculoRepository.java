package com.adrian.taller_app.repository;

import com.adrian.taller_app.domain.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Actualiza km_actual del vehículo solo si el nuevo valor es mayor (o km_actual es null).
     * Atómico en BD: evita que una transacción concurrente sobrescriba un km mayor con uno menor.
     *
     * @return número de filas actualizadas (1 si se actualizó, 0 si no)
     */
    @Modifying
    @Query("UPDATE Vehiculo v SET v.kmActual = :km WHERE v.idVehiculo = :id AND (v.kmActual IS NULL OR v.kmActual < :km)")
    int updateKmActualIfGreater(@Param("id") Long idVehiculo, @Param("km") Integer km);
}
