package com.adrian.taller_app.controller;

import com.adrian.taller_app.domain.RolUsuario;
import com.adrian.taller_app.domain.Usuario;
import com.adrian.taller_app.service.UsuarioService;
import com.adrian.taller_app.web.UsuarioForm;
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
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.adrian.taller_app.domain.Usuario> usuariosPage = usuarioService.findAll(pageable);
        
        model.addAttribute("title", "Usuarios");
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usuariosPage.getTotalPages());
        model.addAttribute("totalItems", usuariosPage.getTotalElements());
        model.addAttribute("pageSize", size);
        return "usuarios/list";
    }

    @GetMapping("/nuevo")
    public String createForm(Model model) {
        model.addAttribute("title", "Nuevo usuario");
        model.addAttribute("usuarioForm", new UsuarioForm());
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("action", "/usuarios");
        model.addAttribute("isEdit", false);
        return "usuarios/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            bindingResult.rejectValue("password", "NotBlank", "La contraseña es obligatoria.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Nuevo usuario");
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("action", "/usuarios");
            model.addAttribute("isEdit", false);
            return "usuarios/form";
        }
        // Validar username duplicado antes de crear
        if (usuarioService.existsByUsername(form.getUsername())) {
            bindingResult.rejectValue("username", "Duplicate", "El nombre de usuario ya existe.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Nuevo usuario");
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("action", "/usuarios");
            model.addAttribute("isEdit", false);
            return "usuarios/form";
        }
        try {
            usuarioService.create(form);
            redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente.");
        } catch (IllegalStateException ex) {
            model.addAttribute("title", "Nuevo usuario");
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("action", "/usuarios");
            model.addAttribute("isEdit", false);
            model.addAttribute("error", ex.getMessage());
            return "usuarios/form";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.findById(id);
            UsuarioForm form = toForm(usuario);
            model.addAttribute("title", "Editar usuario");
            model.addAttribute("usuarioForm", form);
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("action", "/usuarios/" + id);
            model.addAttribute("isEdit", true);
            return "usuarios/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Editar usuario");
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("action", "/usuarios/" + id);
            model.addAttribute("isEdit", true);
            return "usuarios/form";
        }
        // Validar username duplicado antes de actualizar (excluyendo el usuario actual)
        if (usuarioService.existsByUsernameExcludingId(form.getUsername(), id)) {
            bindingResult.rejectValue("username", "Duplicate", "El nombre de usuario ya existe.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Editar usuario");
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("action", "/usuarios/" + id);
            model.addAttribute("isEdit", true);
            return "usuarios/form";
        }
        try {
            usuarioService.update(id, form);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente.");
            return "redirect:/usuarios";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios";
        } catch (IllegalStateException ex) {
            model.addAttribute("title", "Editar usuario");
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("action", "/usuarios/" + id);
            model.addAttribute("isEdit", true);
            model.addAttribute("error", ex.getMessage());
            return "usuarios/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente.");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    private UsuarioForm toForm(Usuario usuario) {
        UsuarioForm form = new UsuarioForm();
        form.setUsername(usuario.getUsername());
        form.setNombre(usuario.getNombre());
        form.setEmail(usuario.getEmail());
        form.setRol(usuario.getRol());
        form.setActivo(Boolean.TRUE.equals(usuario.getActivo()));
        return form;
    }
}
