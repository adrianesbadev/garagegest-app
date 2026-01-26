package com.adrian.taller_app.controller;

import com.adrian.taller_app.domain.Recordatorio;
import com.adrian.taller_app.repository.ClienteRepository;
import com.adrian.taller_app.repository.OrdenTrabajoRepository;
import com.adrian.taller_app.repository.VehiculoRepository;
import com.adrian.taller_app.service.OrdenTrabajoService;
import com.adrian.taller_app.service.RecordatorioService;
import com.adrian.taller_app.web.ResumenFacturacion;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final OrdenTrabajoService ordenTrabajoService;
    private final RecordatorioService recordatorioService;

    public HomeController(ClienteRepository clienteRepository,
                          VehiculoRepository vehiculoRepository,
                          OrdenTrabajoRepository ordenTrabajoRepository,
                          OrdenTrabajoService ordenTrabajoService,
                          RecordatorioService recordatorioService) {
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.ordenTrabajoService = ordenTrabajoService;
        this.recordatorioService = recordatorioService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Inicio");
        return "landing";
    }

    @GetMapping("/resumen")
    public String resumen(Model model, Authentication authentication) {
        model.addAttribute("title", "Dashboard");

        // Datos básicos
        model.addAttribute("clientesRegistrados", clienteRepository.count());
        model.addAttribute("vehiculosRegistrados", vehiculoRepository.count());
        model.addAttribute("ordenesAbiertas", ordenTrabajoRepository.countByFechaCierreIsNull());

        // Determinar rol del usuario
        boolean esAdmin = false;
        boolean esMecanico = false;
        Long idUsuarioActual = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                String rol = authority.getAuthority();
                if (rol.equals("ROLE_ADMIN") || rol.equals("ROLE_RECEPCION")) {
                    esAdmin = true;
                }
                if (rol.equals("ROLE_MECANICO")) {
                    esMecanico = true;
                }
            }
            
        }

        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("esMecanico", esMecanico);

        // Solo para admin y recepción: estadísticas completas
        if (esAdmin) {
            // Resumen de facturación del mes
            ResumenFacturacion resumen = ordenTrabajoService.obtenerResumenFacturacionMesActual();
            model.addAttribute("resumenFacturacion", resumen);

            // Recordatorios próximos
            List<Recordatorio> recordatoriosProximos = recordatorioService.obtenerRecordatoriosProximos(5);
            model.addAttribute("recordatoriosProximos", recordatoriosProximos);
        }

        return "home";
    }
}
