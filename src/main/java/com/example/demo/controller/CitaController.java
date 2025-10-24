package com.example.demo.controller;

import com.example.demo.model.Cita;
import com.example.demo.model.Profesional;
import com.example.demo.model.Servicio;
import com.example.demo.model.Usuario;
import com.example.demo.service.CitaService;
import com.example.demo.service.ProfesionalService;
import com.example.demo.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/citas")
public class CitaController {
    
    private final CitaService citaService;
    private final ServicioService servicioService;
    private final ProfesionalService profesionalService;
    
    public CitaController(CitaService citaService, ServicioService servicioService, 
                         ProfesionalService profesionalService) {
        this.citaService = citaService;
        this.servicioService = servicioService;
        this.profesionalService = profesionalService;
    }
    
    // Mostrar formulario de reserva
    @GetMapping("/reservar/{servicioId}")
    public String mostrarFormularioReserva(@PathVariable Long servicioId, 
                                          Model model, 
                                          HttpSession session) {
        
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login?required";
        }
        
        Optional<Servicio> servicioOpt = servicioService.buscarPorId(servicioId);
        
        if (servicioOpt.isEmpty()) {
            return "redirect:/servicios?error=servicio";
        }
        
        model.addAttribute("servicio", servicioOpt.get());
        model.addAttribute("profesionales", profesionalService.listarTodos());
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        return "reservar-cita";
    }
    
    // Procesar reserva de cita
    @PostMapping("/reservar")
    public String procesarReserva(@RequestParam Long servicioId,
                                 @RequestParam Long profesionalId,
                                 @RequestParam String fechaHora,
                                 HttpSession session,
                                 Model model) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return "redirect:/login?required";
        }
        
        try {
            // Crear la cita
            Cita cita = new Cita();
            cita.setUsuario(usuario);
            
            // Buscar y asignar servicio
            Optional<Servicio> servicioOpt = servicioService.buscarPorId(servicioId);
            if (servicioOpt.isEmpty()) {
                return "redirect:/servicios?error=servicio";
            }
            cita.setServicio(servicioOpt.get());
            
            // Buscar y asignar profesional
            Optional<Profesional> profesionalOpt = profesionalService.buscarPorId(profesionalId);
            if (profesionalOpt.isEmpty()) {
                return "redirect:/servicios?error=profesional";
            }
            cita.setProfesional(profesionalOpt.get());
            
            // Convertir fecha y hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            cita.setFechaHora(LocalDateTime.parse(fechaHora, formatter));
            
            cita.setEstado("PENDIENTE");
            
            // Guardar la cita en la base de datos
            citaService.guardar(cita);
            
            return "redirect:/citas/mis-citas?success";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/servicios?error=reserva";
        }
    }
    
    // Ver mis citas
    @GetMapping("/mis-citas")
    public String misCitas(Model model, HttpSession session) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return "redirect:/login?required";
        }
        
        model.addAttribute("citas", citaService.listarPorUsuario(usuario));
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        return "mis-citas";
    }
}
