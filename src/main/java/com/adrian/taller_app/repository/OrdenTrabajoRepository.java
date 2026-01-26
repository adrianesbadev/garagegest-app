package com.adrian.taller_app.repository;

import com.adrian.taller_app.domain.EstadoOrdenTrabajo;
import com.adrian.taller_app.domain.OrdenTrabajo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente", "usuarioAsignado"})
    List<OrdenTrabajo> findAllByOrderByFechaCreacionDesc();

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente", "usuarioAsignado"})
    Page<OrdenTrabajo> findAllByOrderByFechaCreacionDesc(Pageable pageable);

    boolean existsByUsuarioAsignado_IdUsuario(Long idUsuario);

    long countByFechaCierreIsNull();

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente", "usuarioAsignado"})
    List<OrdenTrabajo> findAllByEstadoOrderByFechaCreacionDesc(EstadoOrdenTrabajo estado);

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente", "usuarioAsignado"})
    Page<OrdenTrabajo> findAllByEstadoOrderByFechaCreacionDesc(EstadoOrdenTrabajo estado, Pageable pageable);

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente", "usuarioAsignado"})
    Optional<OrdenTrabajo> findByIdOt(Long idOt);

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente", "usuarioAsignado"})
    List<OrdenTrabajo> findAllByVehiculo_IdVehiculoOrderByFechaCreacionDesc(Long idVehiculo);

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente", "usuarioAsignado"})
    List<OrdenTrabajo> findAllByVehiculo_Cliente_IdClienteOrderByFechaCreacionDesc(Long idCliente);

    // Queries para dashboard
    @Query("SELECT MONTH(o.fechaCreacion), YEAR(o.fechaCreacion), COUNT(o) " +
           "FROM OrdenTrabajo o " +
           "WHERE o.fechaCreacion >= :desde " +
           "GROUP BY YEAR(o.fechaCreacion), MONTH(o.fechaCreacion) " +
           "ORDER BY YEAR(o.fechaCreacion), MONTH(o.fechaCreacion)")
    List<Object[]> contarOrdenesPorMes(@Param("desde") LocalDateTime desde);

    @Query("SELECT MONTH(o.fechaCierre), YEAR(o.fechaCierre), SUM(o.total) " +
           "FROM OrdenTrabajo o " +
           "WHERE o.fechaCierre >= :desde AND o.estado = 'ENTREGADA' " +
           "GROUP BY YEAR(o.fechaCierre), MONTH(o.fechaCierre) " +
           "ORDER BY YEAR(o.fechaCierre), MONTH(o.fechaCierre)")
    List<Object[]> sumarIngresosPorMes(@Param("desde") LocalDateTime desde);

    @Query("SELECT SUM(o.total) FROM OrdenTrabajo o " +
           "WHERE o.estado = 'ENTREGADA' AND o.fechaCierre >= :inicio AND o.fechaCierre < :fin")
    BigDecimal sumarTotalFacturadoEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT SUM(o.total) FROM OrdenTrabajo o " +
           "WHERE o.estado IN ('ABIERTA', 'EN_CURSO', 'TERMINADA')")
    BigDecimal sumarTotalPendiente();

    @Query("SELECT COUNT(o) FROM OrdenTrabajo o " +
           "WHERE o.estado = 'ENTREGADA' AND o.fechaCierre >= :inicio AND o.fechaCierre < :fin")
    long contarFacturasEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente", "usuarioAsignado"})
    List<OrdenTrabajo> findAllByUsuarioAsignado_IdUsuarioAndEstadoInOrderByFechaCreacionDesc(
        Long idUsuario, List<EstadoOrdenTrabajo> estados);
}
