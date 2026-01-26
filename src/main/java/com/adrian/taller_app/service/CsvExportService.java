package com.adrian.taller_app.service;

import com.adrian.taller_app.domain.Cliente;
import com.adrian.taller_app.domain.OrdenTrabajo;
import com.adrian.taller_app.domain.Vehiculo;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para la exportación de datos a formato CSV.
 * Permite exportar clientes, vehículos y órdenes de trabajo.
 */
@Service
public class CsvExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Exporta una lista de clientes a CSV
     */
    public byte[] exportClientes(List<Cliente> clientes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Añadir BOM UTF-8 para Excel
        baos.write(0xEF);
        baos.write(0xBB);
        baos.write(0xBF);
        
        try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            // Encabezados
            writer.write("Nombre,Teléfono,Email,NIF,Fecha de Alta\n");
            
            // Datos
            for (Cliente cliente : clientes) {
                writer.write(escapeCsv(cliente.getNombre()) + ",");
                writer.write(escapeCsv(cliente.getTelefono() != null ? cliente.getTelefono() : "") + ",");
                writer.write(escapeCsv(cliente.getEmail() != null ? cliente.getEmail() : "") + ",");
                writer.write(escapeCsv(cliente.getNif() != null ? cliente.getNif() : "") + ",");
                writer.write(cliente.getFechaAlta() != null ? cliente.getFechaAlta().format(DATE_FORMATTER) : "");
                writer.write("\n");
            }
        }
        
        return baos.toByteArray();
    }

    /**
     * Exporta una lista de vehículos a CSV
     */
    public byte[] exportVehiculos(List<Vehiculo> vehiculos) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Añadir BOM UTF-8 para Excel
        baos.write(0xEF);
        baos.write(0xBB);
        baos.write(0xBF);
        
        try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            // Encabezados
            writer.write("Matrícula,Marca,Modelo,Año,Kilómetros Actuales,Cliente\n");
            
            // Datos
            for (Vehiculo vehiculo : vehiculos) {
                writer.write(escapeCsv(vehiculo.getMatricula()) + ",");
                writer.write(escapeCsv(vehiculo.getMarca() != null ? vehiculo.getMarca() : "") + ",");
                writer.write(escapeCsv(vehiculo.getModelo() != null ? vehiculo.getModelo() : "") + ",");
                writer.write(vehiculo.getAnio() != null ? vehiculo.getAnio().toString() : "");
                writer.write(",");
                writer.write(vehiculo.getKmActual() != null ? vehiculo.getKmActual().toString() : "");
                writer.write(",");
                writer.write(escapeCsv(vehiculo.getCliente() != null ? vehiculo.getCliente().getNombre() : ""));
                writer.write("\n");
            }
        }
        
        return baos.toByteArray();
    }

    /**
     * Exporta una lista de órdenes de trabajo a CSV
     */
    public byte[] exportOrdenesTrabajo(List<OrdenTrabajo> ordenes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Añadir BOM UTF-8 para Excel
        baos.write(0xEF);
        baos.write(0xBB);
        baos.write(0xBF);
        
        try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            // Encabezados
            writer.write("Nº OT,Matrícula Vehículo,Cliente,Asignado a,Estado,Fecha Creación,Fecha Cierre,Km Entrada,Descripción,Subtotal,IVA Total,Total\n");
            
            // Datos
            for (OrdenTrabajo ot : ordenes) {
                writer.write(ot.getIdOt().toString() + ",");
                writer.write(escapeCsv(ot.getVehiculo() != null ? ot.getVehiculo().getMatricula() : "") + ",");
                writer.write(escapeCsv(ot.getVehiculo() != null && ot.getVehiculo().getCliente() != null 
                    ? ot.getVehiculo().getCliente().getNombre() : "") + ",");
                writer.write(escapeCsv(ot.getUsuarioAsignado() != null ? ot.getUsuarioAsignado().getNombre() : "") + ",");
                writer.write(escapeCsv(ot.getEstado() != null ? ot.getEstado().getEtiqueta() : "") + ",");
                writer.write(ot.getFechaCreacion() != null ? ot.getFechaCreacion().format(DATETIME_FORMATTER) : "");
                writer.write(",");
                writer.write(ot.getFechaCierre() != null ? ot.getFechaCierre().format(DATETIME_FORMATTER) : "");
                writer.write(",");
                writer.write(ot.getKmEntrada() != null ? ot.getKmEntrada().toString() : "");
                writer.write(",");
                writer.write(escapeCsv(ot.getDescripcion() != null ? ot.getDescripcion() : "") + ",");
                writer.write(ot.getSubtotal() != null ? ot.getSubtotal().toString().replace(".", ",") : "");
                writer.write(",");
                writer.write(ot.getIvaTotal() != null ? ot.getIvaTotal().toString().replace(".", ",") : "");
                writer.write(",");
                writer.write(ot.getTotal() != null ? ot.getTotal().toString().replace(".", ",") : "");
                writer.write("\n");
            }
        }
        
        return baos.toByteArray();
    }

    /**
     * Escapa valores CSV (añade comillas si contiene comas, comillas o saltos de línea)
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Si contiene comas, comillas o saltos de línea, envolver en comillas y escapar comillas internas
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
