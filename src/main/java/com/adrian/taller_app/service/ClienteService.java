package com.adrian.taller_app.service;

import com.adrian.taller_app.domain.Cliente;
import com.adrian.taller_app.repository.ClienteRepository;
import com.adrian.taller_app.repository.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de clientes.
 * Maneja operaciones CRUD, validación de NIF y sanitización de datos.
 */
@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    public ClienteService(ClienteRepository clienteRepository, VehiculoRepository vehiculoRepository) {
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll(Sort.by("nombre").ascending());
    }

    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAllByOrderByNombreAsc(pageable);
    }

    public Cliente findById(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    public Cliente findByIdWithVehiculos(Long idCliente) {
        return clienteRepository.findWithVehiculosByIdCliente(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    @Transactional
    public Cliente create(Cliente cliente) {
        sanitize(cliente);
        validateNif(cliente, null);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente update(Long idCliente, Cliente datos) {
        Cliente existente = findById(idCliente);
        sanitize(datos);
        existente.setNombre(datos.getNombre());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());
        existente.setNif(datos.getNif());
        validateNif(existente, idCliente);
        return clienteRepository.save(existente);
    }

    @Transactional
    public void delete(Long idCliente) {
        if (vehiculoRepository.existsByCliente_IdCliente(idCliente)) {
            throw new IllegalStateException("No se puede eliminar el cliente porque tiene vehículos asociados.");
        }
        if (!clienteRepository.existsById(idCliente)) {
            throw new EntityNotFoundException("Cliente no encontrado");
        }
        clienteRepository.deleteById(idCliente);
    }

    private void sanitize(Cliente cliente) {
        cliente.setNombre(clean(cliente.getNombre()));
        cliente.setTelefono(clean(cliente.getTelefono()));
        cliente.setEmail(clean(cliente.getEmail()));
        String nif = clean(cliente.getNif());
        if (nif != null) {
            // Normalizar NIF: mayúsculas y sin espacios
            cliente.setNif(nif.toUpperCase().replaceAll("\\s+", ""));
        } else {
            cliente.setNif(null);
        }
    }

    private void validateNif(Cliente cliente, Long idActual) {
        String nif = cliente.getNif();
        if (nif == null || nif.trim().isEmpty()) {
            return; // NIF es opcional, si está vacío no validamos
        }
        String nifNormalizado = nif.trim().toUpperCase();
        boolean existe = clienteRepository.existsByNifIgnoreCase(nifNormalizado);
        if (!existe) {
            return;
        }
        if (idActual == null) {
            throw new IllegalStateException("Ya existe un cliente con este NIF/CIF.");
        }
        clienteRepository.findByNifIgnoreCase(nifNormalizado)
                .filter(c -> !c.getIdCliente().equals(idActual))
                .ifPresent(c -> {
                    throw new IllegalStateException("Ya existe un cliente con este NIF/CIF.");
                });
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
