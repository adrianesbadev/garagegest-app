package com.adrian.taller_app.repository;

import com.adrian.taller_app.domain.Recordatorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    List<Recordatorio> findAllByOrderByCreadoEnDesc();

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    Page<Recordatorio> findAllByOrderByCreadoEnDesc(Pageable pageable);

    // Recordatorios próximos por fecha
    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    @Query("SELECT r FROM Recordatorio r " +
           "WHERE r.modo IN ('POR_FECHA', 'AMBOS') " +
           "AND r.fechaObjetivo IS NOT NULL " +
           "AND r.fechaObjetivo BETWEEN :hoy AND :limite " +
           "ORDER BY r.fechaObjetivo ASC")
    List<Recordatorio> findRecordatoriosProximosPorFecha(
        @Param("hoy") LocalDate hoy, 
        @Param("limite") LocalDate limite,
        Pageable pageable);

    // Recordatorios próximos por km (dentro de un margen)
    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    @Query("SELECT r FROM Recordatorio r " +
           "WHERE r.modo IN ('POR_KM', 'AMBOS') " +
           "AND r.kmObjetivo IS NOT NULL " +
           "AND r.vehiculo.kmActual IS NOT NULL " +
           "AND (r.kmObjetivo - r.vehiculo.kmActual) BETWEEN 0 AND :margenKm " +
           "ORDER BY (r.kmObjetivo - r.vehiculo.kmActual) ASC")
    List<Recordatorio> findRecordatoriosProximosPorKm(
        @Param("margenKm") int margenKm,
        Pageable pageable);
}
