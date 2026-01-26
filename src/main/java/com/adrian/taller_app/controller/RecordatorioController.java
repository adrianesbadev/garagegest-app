package com.adrian.taller_app.controller;

import com.adrian.taller_app.domain.ModoRecordatorio;
import com.adrian.taller_app.domain.Recordatorio;
import com.adrian.taller_app.domain.Vehiculo;
import com.adrian.taller_app.service.RecordatorioService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/recordatorios")
public class RecordatorioController {

    private final RecordatorioService recordatorioService;
    private final VehiculoService vehiculoService;

    public RecordatorioController(RecordatorioService recordatorioService, VehiculoService vehiculoService) {
        this.recordatorioService = recordatorioService;
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.adrian.taller_app.domain.Recordatorio> recordatoriosPage = recordatorioService.findAll(pageable);
        
        model.addAttribute("title", "Recordatorios");
        model.addAttribute("recordatorios", recordatoriosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", recordatoriosPage.getTotalPages());
        model.addAttribute("totalItems", recordatoriosPage.getTotalElements());
        model.addAttribute("pageSize", size);
        return "recordatorios/list";
    }

    @GetMapping("/nuevo")
    public String createForm(Model model) {
        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setVehiculo(new Vehiculo());
        model.addAttribute("title", "Nuevo recordatorio");
        model.addAttribute("recordatorio", recordatorio);
        loadSelectLists(model);
        model.addAttribute("action", "/recordatorios");
        model.addAttribute("isEdit", false);
        return "recordatorios/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("recordatorio") Recordatorio recordatorio,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateVehiculoSelection(recordatorio, bindingResult);
        validateModoSelection(recordatorio, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Nuevo recordatorio");
            ensureVehiculoNotNull(recordatorio);
            loadSelectLists(model);
            model.addAttribute("action", "/recordatorios");
            model.addAttribute("isEdit", false);
            return "recordatorios/form";
        }
        try {
            recordatorioService.create(recordatorio);
            redirectAttributes.addFlashAttribute("success", "Recordatorio creado correctamente.");
        } catch (EntityNotFoundException | IllegalStateException ex) {
            model.addAttribute("title", "Nuevo recordatorio");
            ensureVehiculoNotNull(recordatorio);
            loadSelectLists(model);
            model.addAttribute("action", "/recordatorios");
            model.addAttribute("isEdit", false);
            model.addAttribute("error", ex.getMessage());
            return "recordatorios/form";
        }
        return "redirect:/recordatorios";
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Recordatorio recordatorio = recordatorioService.findById(id);
            ensureVehiculoNotNull(recordatorio);
            model.addAttribute("title", "Editar recordatorio");
            model.addAttribute("recordatorio", recordatorio);
            loadSelectLists(model);
            model.addAttribute("action", "/recordatorios/" + id);
            model.addAttribute("isEdit", true);
            return "recordatorios/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Recordatorio no encontrado.");
            return "redirect:/recordatorios";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("recordatorio") Recordatorio recordatorio,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateVehiculoSelection(recordatorio, bindingResult);
        validateModoSelection(recordatorio, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Editar recordatorio");
            ensureVehiculoNotNull(recordatorio);
            loadSelectLists(model);
            model.addAttribute("action", "/recordatorios/" + id);
            model.addAttribute("isEdit", true);
            return "recordatorios/form";
        }
        try {
            recordatorioService.update(id, recordatorio);
            redirectAttributes.addFlashAttribute("success", "Recordatorio actualizado correctamente.");
            return "redirect:/recordatorios";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Recordatorio no encontrado.");
            return "redirect:/recordatorios";
        } catch (IllegalStateException ex) {
            model.addAttribute("title", "Editar recordatorio");
            ensureVehiculoNotNull(recordatorio);
            loadSelectLists(model);
            model.addAttribute("action", "/recordatorios/" + id);
            model.addAttribute("isEdit", true);
            model.addAttribute("error", ex.getMessage());
            return "recordatorios/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            recordatorioService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Recordatorio eliminado correctamente.");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Recordatorio no encontrado.");
        }
        return "redirect:/recordatorios";
    }

    private void loadSelectLists(Model model) {
        model.addAttribute("vehiculos", vehiculoService.findAll());
        model.addAttribute("modos", ModoRecordatorio.values());
    }

    private void validateVehiculoSelection(Recordatorio recordatorio, BindingResult bindingResult) {
        if (recordatorio.getVehiculo() == null || recordatorio.getVehiculo().getIdVehiculo() == null) {
            bindingResult.rejectValue("vehiculo", "NotNull", "Selecciona un vehículo.");
        }
    }

    private void validateModoSelection(Recordatorio recordatorio, BindingResult bindingResult) {
        if (recordatorio.getModo() == null) {
            bindingResult.rejectValue("modo", "NotNull", "Selecciona un modo.");
        }
    }

    private void ensureVehiculoNotNull(Recordatorio recordatorio) {
        if (recordatorio.getVehiculo() == null) {
            recordatorio.setVehiculo(new Vehiculo());
        }
    }
}
