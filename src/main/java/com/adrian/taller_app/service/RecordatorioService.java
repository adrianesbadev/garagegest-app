package com.adrian.taller_app.service;

import com.adrian.taller_app.domain.ModoRecordatorio;
import com.adrian.taller_app.domain.Recordatorio;
import com.adrian.taller_app.domain.Vehiculo;
import com.adrian.taller_app.repository.RecordatorioRepository;
import com.adrian.taller_app.repository.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión de recordatorios (ITV, seguros, revisiones).
 * Maneja operaciones CRUD y consultas de recordatorios próximos a vencer.
 */
@Service
@Transactional(readOnly = true)
public class RecordatorioService {

    private final RecordatorioRepository recordatorioRepository;
    private final VehiculoRepository vehiculoRepository;

    public RecordatorioService(RecordatorioRepository recordatorioRepository,
                               VehiculoRepository vehiculoRepository) {
        this.recordatorioRepository = recordatorioRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Recordatorio> findAll() {
        return recordatorioRepository.findAllByOrderByCreadoEnDesc();
    }

    public Page<Recordatorio> findAll(Pageable pageable) {
        return recordatorioRepository.findAllByOrderByCreadoEnDesc(pageable);
    }

    public Recordatorio findById(Long idRecordatorio) {
        return recordatorioRepository.findById(idRecordatorio)
                .orElseThrow(() -> new EntityNotFoundException("Recordatorio no encontrado"));
    }

    @Transactional
    public Recordatorio create(Recordatorio recordatorio) {
        sanitize(recordatorio);
        Vehiculo vehiculo = resolveVehiculo(recordatorio);
        recordatorio.setVehiculo(vehiculo);
        validateObjetivo(recordatorio);
        return recordatorioRepository.save(recordatorio);
    }

    @Transactional
    public Recordatorio update(Long idRecordatorio, Recordatorio datos) {
        Recordatorio existente = findById(idRecordatorio);
        sanitize(datos);
        Vehiculo vehiculo = resolveVehiculo(datos);
        existente.setVehiculo(vehiculo);
        existente.setTipo(datos.getTipo());
        existente.setModo(datos.getModo());
        existente.setFechaObjetivo(datos.getFechaObjetivo());
        existente.setKmObjetivo(datos.getKmObjetivo());
        existente.setEstado(datos.getEstado());
        validateObjetivo(existente);
        return existente;
    }

    @Transactional
    public void delete(Long idRecordatorio) {
        if (!recordatorioRepository.existsById(idRecordatorio)) {
            throw new EntityNotFoundException("Recordatorio no encontrado");
        }
        recordatorioRepository.deleteById(idRecordatorio);
    }

    private Vehiculo resolveVehiculo(Recordatorio recordatorio) {
        if (recordatorio.getVehiculo() == null || recordatorio.getVehiculo().getIdVehiculo() == null) {
            throw new IllegalStateException("El recordatorio debe tener un vehículo.");
        }
        return vehiculoRepository.findById(recordatorio.getVehiculo().getIdVehiculo())
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
    }

    private void validateObjetivo(Recordatorio recordatorio) {
        ModoRecordatorio modo = recordatorio.getModo();
        if (modo == null) {
            throw new IllegalStateException("Selecciona un modo de recordatorio.");
        }
        boolean necesitaFecha = modo == ModoRecordatorio.POR_FECHA || modo == ModoRecordatorio.AMBOS;
        boolean necesitaKm = modo == ModoRecordatorio.POR_KM || modo == ModoRecordatorio.AMBOS;
        if (necesitaFecha && recordatorio.getFechaObjetivo() == null) {
            throw new IllegalStateException("La fecha objetivo es obligatoria para este modo.");
        }
        if (necesitaKm && recordatorio.getKmObjetivo() == null) {
            throw new IllegalStateException("El kilometraje objetivo es obligatorio para este modo.");
        }
        if (necesitaKm && recordatorio.getVehiculo() != null && recordatorio.getVehiculo().getKmActual() != null) {
            Integer kmActual = recordatorio.getVehiculo().getKmActual();
            Integer kmObjetivo = recordatorio.getKmObjetivo();
            if (kmObjetivo != null && kmObjetivo < kmActual) {
                throw new IllegalStateException("El kilometraje objetivo no puede ser menor que el actual del vehículo.");
            }
        }
    }

    private void sanitize(Recordatorio recordatorio) {
        recordatorio.setTipo(clean(recordatorio.getTipo()));
        recordatorio.setEstado(clean(recordatorio.getEstado()));
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Obtiene los recordatorios más próximos a vencer
     * @param limite Número máximo de recordatorios a devolver
     * @return Lista de recordatorios ordenados por urgencia
     */
    public List<Recordatorio> obtenerRecordatoriosProximos(int limite) {
        LocalDate hoy = LocalDate.now();
        LocalDate limiteUrgencia = hoy.plusDays(30);
        int margenKm = 2000;

        // Obtener recordatorios próximos por fecha
        List<Recordatorio> porFecha = recordatorioRepository.findRecordatoriosProximosPorFecha(
            hoy, limiteUrgencia, PageRequest.of(0, limite)
        );

        // Obtener recordatorios próximos por km
        List<Recordatorio> porKm = recordatorioRepository.findRecordatoriosProximosPorKm(
            margenKm, PageRequest.of(0, limite)
        );

        // Combinar y eliminar duplicados
        List<Recordatorio> combinados = new ArrayList<>(porFecha);
        for (Recordatorio r : porKm) {
            if (!combinados.contains(r)) {
                combinados.add(r);
            }
        }

        // Ordenar por urgencia y limitar
        combinados.sort((r1, r2) -> {
            int urgencia1 = calcularUrgencia(r1);
            int urgencia2 = calcularUrgencia(r2);
            return Integer.compare(urgencia1, urgencia2);
        });

        return combinados.stream().limit(limite).toList();
    }

    /**
     * Calcula la urgencia de un recordatorio (menor número = más urgente)
     */
    private int calcularUrgencia(Recordatorio r) {
        int urgencia = Integer.MAX_VALUE;

        // Urgencia por fecha
        if (r.getFechaObjetivo() != null) {
            LocalDate hoy = LocalDate.now();
            long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, r.getFechaObjetivo());
            urgencia = Math.min(urgencia, (int) diasRestantes);
        }

        // Urgencia por km (aproximado a días considerando 50km/día promedio)
        if (r.getKmObjetivo() != null && r.getVehiculo() != null && r.getVehiculo().getKmActual() != null) {
            int kmRestantes = r.getKmObjetivo() - r.getVehiculo().getKmActual();
            int diasEquivalentes = kmRestantes / 50;
            urgencia = Math.min(urgencia, diasEquivalentes);
        }

        return Math.max(0, urgencia);
    }
}
