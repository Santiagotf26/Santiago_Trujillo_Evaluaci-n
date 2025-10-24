package com.example.demo.controller;

import com.example.demo.model.Profesional;
import com.example.demo.service.ProfesionalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/profesionales")
public class ProfesionalController {
    
    private final ProfesionalService profesionalService;
    
    public ProfesionalController(ProfesionalService profesionalService) {
        this.profesionalService = profesionalService;
    }
    
    // Listar todos los profesionales
    @GetMapping
    public String listar(Model model, HttpSession session) {
        
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login?required";
        }
        
        List<Profesional> profesionales = profesionalService.listarTodos();
        model.addAttribute("profesionales", profesionales);
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        return "profesionales";
    }
}
