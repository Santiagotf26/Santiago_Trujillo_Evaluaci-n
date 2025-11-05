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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller  // ✅ IMPORTANTE: Esta anotación
@RequestMapping("/citas")  // ✅ IMPORTANTE: Esta ruta base
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
    
    @GetMapping("/reservar/{servicioId}")
    public String mostrarFormularioReserva(@PathVariable Long servicioId, 
                                          Model model, 
                                          HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
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
    
    @PostMapping("/reservar")
    public String procesarReserva(@RequestParam Long servicioId,
                                 @RequestParam Long profesionalId,
                                 @RequestParam String fechaHora,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login?required";
        }
        
        try {
            Optional<Servicio> servicioOpt = servicioService.buscarPorId(servicioId);
            if (servicioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Servicio no encontrado");
                return "redirect:/servicios";
            }
            
            Optional<Profesional> profesionalOpt = profesionalService.buscarPorId(profesionalId);
            if (profesionalOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Profesional no encontrado");
                return "redirect:/servicios";
            }
            
            Cita cita = new Cita();
            cita.setUsuario(usuario);
            cita.setServicio(servicioOpt.get());
            cita.setProfesional(profesionalOpt.get());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            cita.setFechaHora(LocalDateTime.parse(fechaHora, formatter));
            cita.setEstado("PENDIENTE");
            
            citaService.guardar(cita);
            
            redirectAttributes.addFlashAttribute("success", "Cita agendada exitosamente");
            return "redirect:/citas/mis-citas";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al agendar la cita: " + e.getMessage());
            return "redirect:/servicios";
        }
    }
    
    // ✅ ESTE ES EL MÉTODO CLAVE
    @GetMapping("/mis-citas")
    public String misCitas(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return "redirect:/login?required";
        }
        
        model.addAttribute("citas", citaService.listarPorUsuario(usuario));
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        return "mis-citas";  // ✅ SIN EXTENSIÓN .html
    }
    
    @GetMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id, 
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login?required";
        }
        
        try {
            Optional<Cita> citaOpt = citaService.buscarPorId(id);
            
            if (citaOpt.isPresent()) {
                Cita cita = citaOpt.get();
                
                if (cita.getUsuario().getId().equals(usuario.getId())) {
                    cita.setEstado("CANCELADA");
                    citaService.actualizar(cita);
                    redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
                } else {
                    redirectAttributes.addFlashAttribute("error", "No tienes permiso para cancelar esta cita");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la cita");
        }
        
        return "redirect:/citas/mis-citas";
    }
}
