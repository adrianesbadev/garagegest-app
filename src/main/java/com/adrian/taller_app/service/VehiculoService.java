package com.adrian.taller_app.service;

import com.adrian.taller_app.domain.Cliente;
import com.adrian.taller_app.domain.Vehiculo;
import com.adrian.taller_app.repository.ClienteRepository;
import com.adrian.taller_app.repository.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de vehículos.
 * Maneja operaciones CRUD, validación de matrículas y sanitización de datos.
 */
@Service
@Transactional(readOnly = true)
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository, ClienteRepository clienteRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Vehiculo> findAll() {
        return vehiculoRepository.findAllByOrderByMatriculaAsc();
    }

    public Page<Vehiculo> findAll(Pageable pageable) {
        return vehiculoRepository.findAllByOrderByMatriculaAsc(pageable);
    }

    public Vehiculo findById(Long idVehiculo) {
        return vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
    }

    public Vehiculo findByIdWithCliente(Long idVehiculo) {
        return vehiculoRepository.findWithClienteByIdVehiculo(idVehiculo)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
    }

    public List<Vehiculo> findAllByCliente(Long idCliente) {
        return vehiculoRepository.findAllByCliente_IdClienteOrderByMatriculaAsc(idCliente);
    }

    @Transactional
    public Vehiculo create(Vehiculo vehiculo) {
        sanitize(vehiculo);
        Cliente cliente = resolveCliente(vehiculo);
        vehiculo.setCliente(cliente);
        validateMatricula(vehiculo, null);
        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public Vehiculo update(Long idVehiculo, Vehiculo datos) {
        Vehiculo existente = findById(idVehiculo);
        sanitize(datos);
        Cliente cliente = resolveCliente(datos);
        existente.setCliente(cliente);
        existente.setMatricula(datos.getMatricula());
        existente.setMarca(datos.getMarca());
        existente.setModelo(datos.getModelo());
        existente.setAnio(datos.getAnio());
        existente.setKmActual(datos.getKmActual());
        validateMatricula(existente, idVehiculo);
        return vehiculoRepository.save(existente);
    }

    @Transactional
    public void delete(Long idVehiculo) {
        if (!vehiculoRepository.existsById(idVehiculo)) {
            throw new EntityNotFoundException("Vehículo no encontrado");
        }
        vehiculoRepository.deleteById(idVehiculo);
    }

    private Cliente resolveCliente(Vehiculo vehiculo) {
        if (vehiculo.getCliente() == null || vehiculo.getCliente().getIdCliente() == null) {
            throw new IllegalStateException("El vehículo debe tener un cliente asignado.");
        }
        return clienteRepository.findById(vehiculo.getCliente().getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    private void validateMatricula(Vehiculo vehiculo, Long idActual) {
        String matricula = vehiculo.getMatricula();
        if (matricula == null) {
            return;
        }
        boolean existe = vehiculoRepository.existsByMatriculaIgnoreCase(matricula);
        if (!existe) {
            return;
        }
        if (idActual == null) {
            throw new IllegalStateException("La matrícula ya existe.");
        }
        vehiculoRepository.findByMatriculaIgnoreCase(matricula)
                .filter(v -> !v.getIdVehiculo().equals(idActual))
                .ifPresent(v -> {
                    throw new IllegalStateException("La matrícula ya existe.");
                });
    }

    private void sanitize(Vehiculo vehiculo) {
        String matricula = clean(vehiculo.getMatricula());
        if (matricula != null) {
            // Normalizar matrícula: mayúsculas, sin espacios y sin guiones
            vehiculo.setMatricula(matricula.toUpperCase()
                    .replaceAll("\\s+", "")
                    .replaceAll("-", ""));
        } else {
            vehiculo.setMatricula(null);
        }
        vehiculo.setMarca(clean(vehiculo.getMarca()));
        vehiculo.setModelo(clean(vehiculo.getModelo()));
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
