package com.adrian.taller_app.service;

import com.adrian.taller_app.domain.EstadoOrdenTrabajo;
import com.adrian.taller_app.domain.OrdenTrabajo;
import com.adrian.taller_app.domain.Usuario;
import com.adrian.taller_app.domain.Vehiculo;
import com.adrian.taller_app.repository.OrdenTrabajoRepository;
import com.adrian.taller_app.repository.UsuarioRepository;
import com.adrian.taller_app.repository.VehiculoRepository;
import com.adrian.taller_app.web.EstadisticasMes;
import com.adrian.taller_app.web.IngresosMes;
import com.adrian.taller_app.web.ResumenFacturacion;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión de órdenes de trabajo.
 * Maneja operaciones CRUD, cálculos financieros, estados y estadísticas.
 */
@Service
@Transactional(readOnly = true)
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    public OrdenTrabajoService(OrdenTrabajoRepository ordenTrabajoRepository,
                               VehiculoRepository vehiculoRepository,
                               UsuarioRepository usuarioRepository) {
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<OrdenTrabajo> findAll() {
        return ordenTrabajoRepository.findAllByOrderByFechaCreacionDesc();
    }

    public List<OrdenTrabajo> findAllByVehiculo(Long idVehiculo) {
        return ordenTrabajoRepository.findAllByVehiculo_IdVehiculoOrderByFechaCreacionDesc(idVehiculo);
    }

    public List<OrdenTrabajo> findAllByCliente(Long idCliente) {
        return ordenTrabajoRepository.findAllByVehiculo_Cliente_IdClienteOrderByFechaCreacionDesc(idCliente);
    }

    public List<OrdenTrabajo> findAllByEstado(EstadoOrdenTrabajo estado) {
        if (estado == null) {
            return findAll();
        }
        return ordenTrabajoRepository.findAllByEstadoOrderByFechaCreacionDesc(estado);
    }

    public Page<OrdenTrabajo> findAllByEstado(EstadoOrdenTrabajo estado, Pageable pageable) {
        if (estado == null) {
            return ordenTrabajoRepository.findAllByOrderByFechaCreacionDesc(pageable);
        }
        return ordenTrabajoRepository.findAllByEstadoOrderByFechaCreacionDesc(estado, pageable);
    }

    public OrdenTrabajo findById(Long idOt) {
        return ordenTrabajoRepository.findById(idOt)
                .orElseThrow(() -> new EntityNotFoundException("Orden de trabajo no encontrada"));
    }

    public OrdenTrabajo findByIdWithRelations(Long idOt) {
        return ordenTrabajoRepository.findByIdOt(idOt)
                .orElseThrow(() -> new EntityNotFoundException("Orden de trabajo no encontrada"));
    }

    @Transactional
    public OrdenTrabajo create(OrdenTrabajo ordenTrabajo) {
        sanitize(ordenTrabajo);
        Vehiculo vehiculo = resolveVehiculo(ordenTrabajo);
        validarKmEntrada(vehiculo, ordenTrabajo.getKmEntrada());
        ordenTrabajo.setVehiculo(vehiculo);
        Usuario usuarioAsignado = resolveUsuarioAsignado(ordenTrabajo.getUsuarioAsignado());
        ordenTrabajo.setUsuarioAsignado(usuarioAsignado);
        ensureEstado(ordenTrabajo);
        handleFechaCierre(ordenTrabajo);
        normalizarTotales(ordenTrabajo);
        OrdenTrabajo saved = ordenTrabajoRepository.save(ordenTrabajo);
        actualizarKmVehiculo(vehiculo, ordenTrabajo.getKmEntrada());
        return saved;
    }

    @Transactional
    public OrdenTrabajo update(Long idOt, OrdenTrabajo datos) {
        OrdenTrabajo existente = findById(idOt);
        sanitize(datos);
        Vehiculo vehiculo = resolveVehiculo(datos);
        validarKmEntrada(vehiculo, datos.getKmEntrada());
        Usuario usuarioAsignado = resolveUsuarioAsignado(datos.getUsuarioAsignado());
        existente.setVehiculo(vehiculo);
        existente.setUsuarioAsignado(usuarioAsignado);
        existente.setEstado(datos.getEstado());
        existente.setKmEntrada(datos.getKmEntrada());
        existente.setDescripcion(datos.getDescripcion());
        existente.setSubtotal(datos.getSubtotal());
        existente.setIvaTotal(datos.getIvaTotal());
        existente.setTotal(datos.getTotal());
        ensureEstado(existente);
        // Manejar fechaCierre: si el usuario establece una manualmente, usarla; si no, establecerla automáticamente si está entregada
        if (datos.getFechaCierre() != null) {
            existente.setFechaCierre(datos.getFechaCierre());
        } else {
            handleFechaCierre(existente);
        }
        normalizarTotales(existente);
        OrdenTrabajo saved = ordenTrabajoRepository.save(existente);
        actualizarKmVehiculo(vehiculo, existente.getKmEntrada());
        return saved;
    }

    /**
     * Actualiza el kilometraje actual del vehículo si la OT tiene km de entrada
     * y es mayor que el km actual del vehículo (evita tener que actualizarlo manualmente).
     */
    private void actualizarKmVehiculo(Vehiculo vehiculo, Integer kmEntrada) {
        if (vehiculo == null || kmEntrada == null) {
            return;
        }
        Integer kmActual = vehiculo.getKmActual();
        if (kmActual == null || kmEntrada > kmActual) {
            vehiculo.setKmActual(kmEntrada);
            vehiculoRepository.save(vehiculo);
        }
    }

    @Transactional
    public void delete(Long idOt) {
        if (!ordenTrabajoRepository.existsById(idOt)) {
            throw new EntityNotFoundException("Orden de trabajo no encontrada");
        }
        ordenTrabajoRepository.deleteById(idOt);
    }

    private Vehiculo resolveVehiculo(OrdenTrabajo ordenTrabajo) {
        if (ordenTrabajo.getVehiculo() == null || ordenTrabajo.getVehiculo().getIdVehiculo() == null) {
            throw new IllegalStateException("La orden de trabajo debe tener un vehículo.");
        }
        return vehiculoRepository.findById(ordenTrabajo.getVehiculo().getIdVehiculo())
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
    }

    private Usuario resolveUsuarioAsignado(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            return null;
        }
        return usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario asignado no encontrado"));
    }

    /**
     * Valida que el km de entrada no sea menor que el km actual del vehículo.
     */
    private void validarKmEntrada(Vehiculo vehiculo, Integer kmEntrada) {
        if (vehiculo == null || kmEntrada == null) {
            return;
        }
        Integer kmActual = vehiculo.getKmActual();
        if (kmActual != null && kmEntrada < kmActual) {
            throw new IllegalStateException(
                    "El kilometraje de entrada (" + kmEntrada + " km) no puede ser menor que el kilometraje actual del vehículo (" + kmActual + " km).");
        }
    }

    private void normalizarTotales(OrdenTrabajo ordenTrabajo) {
        BigDecimal subtotal = ordenTrabajo.getSubtotal();
        if (subtotal == null) {
            ordenTrabajo.setIvaTotal(null);
            ordenTrabajo.setTotal(null);
            return;
        }
        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("El subtotal no puede ser negativo.");
        }
        BigDecimal subtotalScaled = scale(subtotal);
        BigDecimal iva = scale(subtotalScaled.multiply(new BigDecimal("0.21")));
        BigDecimal total = scale(subtotalScaled.add(iva));

        ordenTrabajo.setSubtotal(subtotalScaled);
        ordenTrabajo.setIvaTotal(iva);
        ordenTrabajo.setTotal(total);
    }

    private void ensureEstado(OrdenTrabajo ordenTrabajo) {
        if (ordenTrabajo.getEstado() == null) {
            ordenTrabajo.setEstado(EstadoOrdenTrabajo.ABIERTA);
        }
    }

    private void handleFechaCierre(OrdenTrabajo ordenTrabajo) {
        if (ordenTrabajo.getEstado() == EstadoOrdenTrabajo.ENTREGADA) {
            // Si está entregada y no tiene fecha de cierre, establecerla ahora
            if (ordenTrabajo.getFechaCierre() == null) {
                ordenTrabajo.setFechaCierre(LocalDateTime.now());
            }
        }
        // Si no está entregada, mantener la fechaCierre existente (no limpiarla para preservar historial)
    }

    private BigDecimal scale(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private void sanitize(OrdenTrabajo ordenTrabajo) {
        ordenTrabajo.setDescripcion(clean(ordenTrabajo.getDescripcion()));
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Obtiene estadísticas mensuales de órdenes de trabajo.
     *
     * @param meses número de meses hacia atrás a considerar
     * @return lista de estadísticas por mes
     */
    public List<EstadisticasMes> obtenerEstadisticasMensuales(int meses) {
        LocalDateTime desde = LocalDateTime.now().minusMonths(meses);
        List<Object[]> resultados = ordenTrabajoRepository.contarOrdenesPorMes(desde);
        List<EstadisticasMes> estadisticas = new ArrayList<>();
        
        for (Object[] fila : resultados) {
            int mes = (Integer) fila[0];
            int anio = (Integer) fila[1];
            long cantidad = ((Number) fila[2]).longValue();
            estadisticas.add(new EstadisticasMes(mes, anio, cantidad));
        }
        
        return estadisticas;
    }

    /**
     * Obtiene ingresos mensuales de órdenes de trabajo entregadas.
     *
     * @param meses número de meses hacia atrás a considerar
     * @return lista de ingresos por mes
     */
    public List<IngresosMes> obtenerIngresosMensuales(int meses) {
        LocalDateTime desde = LocalDateTime.now().minusMonths(meses);
        List<Object[]> resultados = ordenTrabajoRepository.sumarIngresosPorMes(desde);
        List<IngresosMes> ingresos = new ArrayList<>();
        
        for (Object[] fila : resultados) {
            int mes = (Integer) fila[0];
            int anio = (Integer) fila[1];
            BigDecimal total = (BigDecimal) fila[2];
            ingresos.add(new IngresosMes(mes, anio, total));
        }
        
        return ingresos;
    }

    /**
     * Obtiene el resumen de facturación del mes actual.
     *
     * @return resumen con total facturado, pendiente y ticket medio
     */
    public ResumenFacturacion obtenerResumenFacturacionMesActual() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioMes = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMes = inicioMes.plusMonths(1);

        BigDecimal totalFacturado = ordenTrabajoRepository.sumarTotalFacturadoEnPeriodo(inicioMes, finMes);
        BigDecimal totalPendiente = ordenTrabajoRepository.sumarTotalPendiente();
        long numeroFacturas = ordenTrabajoRepository.contarFacturasEnPeriodo(inicioMes, finMes);
        
        BigDecimal ticketMedio = BigDecimal.ZERO;
        if (numeroFacturas > 0 && totalFacturado != null) {
            ticketMedio = totalFacturado.divide(
                BigDecimal.valueOf(numeroFacturas), 
                2, 
                RoundingMode.HALF_UP
            );
        }

        return new ResumenFacturacion(
            totalFacturado != null ? totalFacturado : BigDecimal.ZERO,
            totalPendiente != null ? totalPendiente : BigDecimal.ZERO,
            numeroFacturas,
            ticketMedio
        );
    }

    public List<OrdenTrabajo> obtenerOrdenesPendientesPorMecanico(Long idUsuario) {
        List<EstadoOrdenTrabajo> estadosPendientes = List.of(
            EstadoOrdenTrabajo.ABIERTA,
            EstadoOrdenTrabajo.EN_CURSO,
            EstadoOrdenTrabajo.TERMINADA
        );
        return ordenTrabajoRepository.findAllByUsuarioAsignado_IdUsuarioAndEstadoInOrderByFechaCreacionDesc(
            idUsuario, estadosPendientes
        );
    }
}
