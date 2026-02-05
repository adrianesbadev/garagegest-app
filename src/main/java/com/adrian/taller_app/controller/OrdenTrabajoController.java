package com.adrian.taller_app.controller;

import com.adrian.taller_app.domain.EstadoOrdenTrabajo;
import com.adrian.taller_app.domain.OrdenTrabajo;
import com.adrian.taller_app.domain.Usuario;
import com.adrian.taller_app.domain.Vehiculo;
import com.adrian.taller_app.service.CsvExportService;
import com.adrian.taller_app.service.FacturaPdfService;
import com.adrian.taller_app.service.OrdenTrabajoService;
import com.adrian.taller_app.service.UsuarioService;
import com.adrian.taller_app.service.VehiculoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

@Controller
@RequestMapping("/ordenes-trabajo")
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenTrabajoService;
    private final VehiculoService vehiculoService;
    private final UsuarioService usuarioService;
    private final FacturaPdfService facturaPdfService;
    private final CsvExportService csvExportService;

    public OrdenTrabajoController(OrdenTrabajoService ordenTrabajoService,
                                  VehiculoService vehiculoService,
                                  UsuarioService usuarioService,
                                  FacturaPdfService facturaPdfService,
                                  CsvExportService csvExportService) {
        this.ordenTrabajoService = ordenTrabajoService;
        this.vehiculoService = vehiculoService;
        this.usuarioService = usuarioService;
        this.facturaPdfService = facturaPdfService;
        this.csvExportService = csvExportService;
    }

    @GetMapping
    public String list(@RequestParam(name = "estado", required = false) EstadoOrdenTrabajo estado,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrdenTrabajo> ordenesPage = ordenTrabajoService.findAllByEstado(estado, pageable);
        
        model.addAttribute("title", "Órdenes de trabajo");
        model.addAttribute("ordenes", ordenesPage.getContent());
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("estados", EstadoOrdenTrabajo.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordenesPage.getTotalPages());
        model.addAttribute("totalItems", ordenesPage.getTotalElements());
        model.addAttribute("pageSize", size);
        return "ordenes-trabajo/list";
    }

    @GetMapping("/nueva")
    public String createForm(@RequestParam(required = false) Long vehiculoId,
                             Model model) {
        OrdenTrabajo ordenTrabajo = new OrdenTrabajo();
        Vehiculo vehiculo = new Vehiculo();
        
        // Si se proporciona un vehiculoId, pre-seleccionarlo
        if (vehiculoId != null) {
            try {
                Vehiculo vehiculoSeleccionado = vehiculoService.findById(vehiculoId);
                vehiculo = vehiculoSeleccionado;
            } catch (EntityNotFoundException ex) {
                // Si el vehículo no existe, usar vehículo vacío
            }
        }
        
        ordenTrabajo.setVehiculo(vehiculo);
        ordenTrabajo.setUsuarioAsignado(new Usuario());
        model.addAttribute("title", "Nueva orden de trabajo");
        model.addAttribute("ordenTrabajo", ordenTrabajo);
        loadSelectLists(model);
        model.addAttribute("action", "/ordenes-trabajo");
        model.addAttribute("isEdit", false);
        return "ordenes-trabajo/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("ordenTrabajo") OrdenTrabajo ordenTrabajo,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateVehiculoSelection(ordenTrabajo, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Nueva orden de trabajo");
            ensureRelationsNotNull(ordenTrabajo);
            loadSelectLists(model);
            model.addAttribute("action", "/ordenes-trabajo");
            model.addAttribute("isEdit", false);
            return "ordenes-trabajo/form";
        }
        try {
            ordenTrabajoService.create(ordenTrabajo);
            redirectAttributes.addFlashAttribute("success", "Orden de trabajo creada correctamente.");
        } catch (EntityNotFoundException | IllegalStateException ex) {
            model.addAttribute("title", "Nueva orden de trabajo");
            ensureRelationsNotNull(ordenTrabajo);
            loadSelectLists(model);
            model.addAttribute("action", "/ordenes-trabajo");
            model.addAttribute("isEdit", false);
            model.addAttribute("error", ex.getMessage());
            return "ordenes-trabajo/form";
        }
        return "redirect:/ordenes-trabajo";
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            OrdenTrabajo ordenTrabajo = ordenTrabajoService.findById(id);
            ensureRelationsNotNull(ordenTrabajo);
            model.addAttribute("title", "Editar orden de trabajo");
            model.addAttribute("ordenTrabajo", ordenTrabajo);
            model.addAttribute("kmEntradaOriginal", ordenTrabajo.getKmEntrada());
            loadSelectLists(model);
            model.addAttribute("action", "/ordenes-trabajo/" + id);
            model.addAttribute("isEdit", true);
            return "ordenes-trabajo/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Orden de trabajo no encontrada.");
            return "redirect:/ordenes-trabajo";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("ordenTrabajo") OrdenTrabajo ordenTrabajo,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateVehiculoSelection(ordenTrabajo, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Editar orden de trabajo");
            ensureRelationsNotNull(ordenTrabajo);
            model.addAttribute("kmEntradaOriginal", ordenTrabajoService.findById(id).getKmEntrada());
            loadSelectLists(model);
            model.addAttribute("action", "/ordenes-trabajo/" + id);
            model.addAttribute("isEdit", true);
            return "ordenes-trabajo/form";
        }
        try {
            ordenTrabajoService.update(id, ordenTrabajo);
            redirectAttributes.addFlashAttribute("success", "Orden de trabajo actualizada correctamente.");
            return "redirect:/ordenes-trabajo";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Orden de trabajo no encontrada.");
            return "redirect:/ordenes-trabajo";
        } catch (IllegalStateException ex) {
            model.addAttribute("title", "Editar orden de trabajo");
            ensureRelationsNotNull(ordenTrabajo);
            model.addAttribute("kmEntradaOriginal", ordenTrabajoService.findById(id).getKmEntrada());
            loadSelectLists(model);
            model.addAttribute("action", "/ordenes-trabajo/" + id);
            model.addAttribute("isEdit", true);
            model.addAttribute("error", ex.getMessage());
            return "ordenes-trabajo/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            ordenTrabajoService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Orden de trabajo eliminada correctamente.");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Orden de trabajo no encontrada.");
        }
        return "redirect:/ordenes-trabajo";
    }

    @GetMapping("/{id}/factura")
    public ResponseEntity<byte[]> factura(@PathVariable("id") Long id) {
        OrdenTrabajo ordenTrabajo = ordenTrabajoService.findByIdWithRelations(id);
        byte[] pdf = facturaPdfService.generarFactura(ordenTrabajo);
        String filename = "factura-ot-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarCsv(@RequestParam(name = "estado", required = false) EstadoOrdenTrabajo estado) {
        try {
            var ordenes = ordenTrabajoService.findAllByEstado(estado);
            byte[] csvData = csvExportService.exportOrdenesTrabajo(ordenes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            String filename = "ordenes_trabajo_" + 
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private void loadSelectLists(Model model) {
        model.addAttribute("vehiculos", vehiculoService.findAll());
        model.addAttribute("usuarios", usuarioService.findMecanicosActivos());
        model.addAttribute("estados", EstadoOrdenTrabajo.values());
    }

    private void validateVehiculoSelection(OrdenTrabajo ordenTrabajo, BindingResult bindingResult) {
        if (ordenTrabajo.getVehiculo() == null || ordenTrabajo.getVehiculo().getIdVehiculo() == null) {
            bindingResult.rejectValue("vehiculo", "NotNull", "Selecciona un vehículo.");
        }
    }

    private void ensureRelationsNotNull(OrdenTrabajo ordenTrabajo) {
        if (ordenTrabajo.getVehiculo() == null) {
            ordenTrabajo.setVehiculo(new Vehiculo());
        }
        if (ordenTrabajo.getUsuarioAsignado() == null) {
            ordenTrabajo.setUsuarioAsignado(new Usuario());
        }
    }
}
