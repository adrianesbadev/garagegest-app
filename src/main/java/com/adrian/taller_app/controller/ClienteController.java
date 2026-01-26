package com.adrian.taller_app.controller;

import com.adrian.taller_app.domain.Cliente;
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
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;
    private final OrdenTrabajoService ordenTrabajoService;
    private final CsvExportService csvExportService;

    public ClienteController(ClienteService clienteService,
                             VehiculoService vehiculoService,
                             OrdenTrabajoService ordenTrabajoService,
                             CsvExportService csvExportService) {
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
        this.ordenTrabajoService = ordenTrabajoService;
        this.csvExportService = csvExportService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientesPage = clienteService.findAll(pageable);
        
        model.addAttribute("title", "Clientes");
        model.addAttribute("clientes", clientesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", clientesPage.getTotalPages());
        model.addAttribute("totalItems", clientesPage.getTotalElements());
        model.addAttribute("pageSize", size);
        return "clientes/list";
    }

    @GetMapping("/nuevo")
    public String createForm(Model model) {
        model.addAttribute("title", "Nuevo cliente");
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("action", "/clientes");
        model.addAttribute("isEdit", false);
        return "clientes/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("cliente") Cliente cliente,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Nuevo cliente");
            model.addAttribute("action", "/clientes");
            model.addAttribute("isEdit", false);
            return "clientes/form";
        }
        clienteService.create(cliente);
        redirectAttributes.addFlashAttribute("success", "Cliente creado correctamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.findById(id);
            model.addAttribute("title", "Editar cliente");
            model.addAttribute("cliente", cliente);
            model.addAttribute("action", "/clientes/" + id);
            model.addAttribute("isEdit", true);
            return "clientes/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/clientes";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.findByIdWithVehiculos(id);
            model.addAttribute("title", "Cliente " + cliente.getNombre());
            model.addAttribute("cliente", cliente);
            model.addAttribute("vehiculos", vehiculoService.findAllByCliente(id));
            model.addAttribute("ordenes", ordenTrabajoService.findAllByCliente(id));
            return "clientes/detail";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/clientes";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("cliente") Cliente cliente,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Editar cliente");
            model.addAttribute("action", "/clientes/" + id);
            model.addAttribute("isEdit", true);
            return "clientes/form";
        }
        try {
            clienteService.update(id, cliente);
            redirectAttributes.addFlashAttribute("success", "Cliente actualizado correctamente.");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
        }
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado correctamente.");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/clientes";
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarCsv() {
        try {
            List<Cliente> clientes = clienteService.findAll();
            byte[] csvData = csvExportService.exportClientes(clientes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "clientes_" + 
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
