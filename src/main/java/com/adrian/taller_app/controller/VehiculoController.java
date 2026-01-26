package com.adrian.taller_app.controller;

import com.adrian.taller_app.domain.Cliente;
import com.adrian.taller_app.domain.Vehiculo;
import com.adrian.taller_app.service.ClienteService;
import com.adrian.taller_app.service.CsvExportService;
import com.adrian.taller_app.service.OrdenTrabajoService;
import com.adrian.taller_app.service.VehiculoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final ClienteService clienteService;
    private final OrdenTrabajoService ordenTrabajoService;
    private final CsvExportService csvExportService;

    public VehiculoController(VehiculoService vehiculoService,
                              ClienteService clienteService,
                              OrdenTrabajoService ordenTrabajoService,
                              CsvExportService csvExportService) {
        this.vehiculoService = vehiculoService;
        this.clienteService = clienteService;
        this.ordenTrabajoService = ordenTrabajoService;
        this.csvExportService = csvExportService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.adrian.taller_app.domain.Vehiculo> vehiculosPage = vehiculoService.findAll(pageable);
        
        model.addAttribute("title", "Vehículos");
        model.addAttribute("vehiculos", vehiculosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vehiculosPage.getTotalPages());
        model.addAttribute("totalItems", vehiculosPage.getTotalElements());
        model.addAttribute("pageSize", size);
        return "vehiculos/list";
    }

    @GetMapping("/nuevo")
    public String createForm(@org.springframework.web.bind.annotation.RequestParam(required = false) Long clienteId,
                             Model model) {
        Vehiculo vehiculo = new Vehiculo();
        Cliente cliente = new Cliente();
        
        // Si se proporciona un clienteId, pre-seleccionarlo
        if (clienteId != null) {
            try {
                Cliente clienteSeleccionado = clienteService.findById(clienteId);
                cliente = clienteSeleccionado;
            } catch (EntityNotFoundException ex) {
                // Si el cliente no existe, usar cliente vacío
            }
        }
        
        vehiculo.setCliente(cliente);
        model.addAttribute("title", "Nuevo vehículo");
        model.addAttribute("vehiculo", vehiculo);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("action", "/vehiculos");
        model.addAttribute("isEdit", false);
        return "vehiculos/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("vehiculo") Vehiculo vehiculo,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateClienteSelection(vehiculo, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Nuevo vehículo");
            ensureClienteNotNull(vehiculo);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("action", "/vehiculos");
            model.addAttribute("isEdit", false);
            return "vehiculos/form";
        }
        try {
            vehiculoService.create(vehiculo);
            redirectAttributes.addFlashAttribute("success", "Vehículo creado correctamente.");
        } catch (EntityNotFoundException | IllegalStateException ex) {
            model.addAttribute("title", "Nuevo vehículo");
            ensureClienteNotNull(vehiculo);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("action", "/vehiculos");
            model.addAttribute("isEdit", false);
            model.addAttribute("error", ex.getMessage());
            return "vehiculos/form";
        }
        return "redirect:/vehiculos";
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Vehiculo vehiculo = vehiculoService.findById(id);
            model.addAttribute("title", "Editar vehículo");
            model.addAttribute("vehiculo", vehiculo);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("action", "/vehiculos/" + id);
            model.addAttribute("isEdit", true);
            return "vehiculos/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Vehículo no encontrado.");
            return "redirect:/vehiculos";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Vehiculo vehiculo = vehiculoService.findByIdWithCliente(id);
            model.addAttribute("title", "Vehículo " + vehiculo.getMatricula());
            model.addAttribute("vehiculo", vehiculo);
            model.addAttribute("ordenes", ordenTrabajoService.findAllByVehiculo(id));
            return "vehiculos/detail";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Vehículo no encontrado.");
            return "redirect:/vehiculos";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("vehiculo") Vehiculo vehiculo,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateClienteSelection(vehiculo, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Editar vehículo");
            ensureClienteNotNull(vehiculo);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("action", "/vehiculos/" + id);
            model.addAttribute("isEdit", true);
            return "vehiculos/form";
        }
        try {
            vehiculoService.update(id, vehiculo);
            redirectAttributes.addFlashAttribute("success", "Vehículo actualizado correctamente.");
            return "redirect:/vehiculos";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Vehículo no encontrado.");
            return "redirect:/vehiculos";
        } catch (IllegalStateException ex) {
            model.addAttribute("title", "Editar vehículo");
            ensureClienteNotNull(vehiculo);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("action", "/vehiculos/" + id);
            model.addAttribute("isEdit", true);
            model.addAttribute("error", ex.getMessage());
            return "vehiculos/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            vehiculoService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Vehículo eliminado correctamente.");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Vehículo no encontrado.");
        }
        return "redirect:/vehiculos";
    }

    private void validateClienteSelection(Vehiculo vehiculo, BindingResult bindingResult) {
        if (vehiculo.getCliente() == null || vehiculo.getCliente().getIdCliente() == null) {
            bindingResult.rejectValue("cliente", "NotNull", "Selecciona un cliente.");
        }
    }

    private void ensureClienteNotNull(Vehiculo vehiculo) {
        if (vehiculo.getCliente() == null) {
            vehiculo.setCliente(new Cliente());
        }
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarCsv() {
        try {
            var vehiculos = vehiculoService.findAll();
            byte[] csvData = csvExportService.exportVehiculos(vehiculos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "vehiculos_" + 
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
