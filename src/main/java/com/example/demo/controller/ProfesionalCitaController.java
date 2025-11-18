package com.example.demo.controller;

import com.example.demo.model.Cita;
import com.example.demo.model.Profesional;
import com.example.demo.model.Usuario;
import com.example.demo.service.CitaService;
import com.example.demo.service.ProfesionalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profesional")
public class ProfesionalCitaController {

    private final CitaService citaService;
    private final ProfesionalService profesionalService;

    public ProfesionalCitaController(CitaService citaService, ProfesionalService profesionalService) {
        this.citaService = citaService;
        this.profesionalService = profesionalService;
    }

    @GetMapping("/citas")
    public String verCitasProfesional(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login?required";
        }

        if (!"PROFESIONAL".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/servicios";
        }

        Profesional profesional = profesionalService.buscarPorUsuario(usuario);
        if (profesional == null) {
            return "redirect:/servicios";
        }

        List<Cita> citas = citaService.listarPorProfesionalOrdenadas(profesional);
        model.addAttribute("citas", citas);
        model.addAttribute("nombreProfesional", usuario.getNombre());

        return "profesional/citas";
    }

    @GetMapping("/citas/confirmar/{id}")
    public String confirmarCita(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login?required";
        }

        if (!"PROFESIONAL".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/servicios";
        }

        Profesional profesional = profesionalService.buscarPorUsuario(usuario);
        if (profesional == null) {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró información del profesional");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/servicios";
        }

        try {
            Optional<Cita> citaOpt = citaService.buscarPorId(id);
            if (citaOpt.isPresent()) {
                Cita cita = citaOpt.get();

                if (cita.getProfesional() != null &&
                    profesional.getId().equals(cita.getProfesional().getId())) {

                    cita.setEstado("CONFIRMADA");
                    citaService.actualizar(cita);

                    redirectAttributes.addFlashAttribute("mensaje", "Cita confirmada correctamente");
                    redirectAttributes.addFlashAttribute("tipo", "success");
                } else {
                    redirectAttributes.addFlashAttribute("mensaje", "No tienes permiso para confirmar esta cita");
                    redirectAttributes.addFlashAttribute("tipo", "danger");
                }
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Cita no encontrada");
                redirectAttributes.addFlashAttribute("tipo", "danger");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al confirmar la cita");
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }

        return "redirect:/profesional/citas";
    }

    @GetMapping("/citas/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login?required";
        }

        if (!"PROFESIONAL".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/servicios";
        }

        Profesional profesional = profesionalService.buscarPorUsuario(usuario);
        if (profesional == null) {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró información del profesional");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/servicios";
        }

        try {
            Optional<Cita> citaOpt = citaService.buscarPorId(id);
            if (citaOpt.isPresent()) {
                Cita cita = citaOpt.get();

                if (cita.getProfesional() != null &&
                    profesional.getId().equals(cita.getProfesional().getId())) {

                    cita.setEstado("CANCELADA");
                    citaService.actualizar(cita);

                    redirectAttributes.addFlashAttribute("mensaje", "Cita cancelada correctamente");
                    redirectAttributes.addFlashAttribute("tipo", "success");
                } else {
                    redirectAttributes.addFlashAttribute("mensaje", "No tienes permiso para cancelar esta cita");
                    redirectAttributes.addFlashAttribute("tipo", "danger");
                }
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Cita no encontrada");
                redirectAttributes.addFlashAttribute("tipo", "danger");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cancelar la cita");
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }

        return "redirect:/profesional/citas";
    }
}
