package com.example.demo.controller;

import com.example.demo.model.Servicio;
import com.example.demo.service.ServicioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/servicios")
public class ServicioController {
    
    private final ServicioService servicioService;
    
    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }
    
    @GetMapping
    public String lista(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "6") int size,
                       @RequestParam(required = false) String buscar,
                       Model model,
                       HttpSession session) {
        
        // Verificar si el usuario está logueado
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login?required";
        }
        
        // Crear objeto Pageable
        Pageable pageable = PageRequest.of(page, size);
        
        // Buscar servicios con o sin filtro
        Page<Servicio> serviciosPage;
        if (buscar != null && !buscar.trim().isEmpty()) {
            serviciosPage = servicioService.buscarPorNombre(buscar, pageable);
            model.addAttribute("buscar", buscar);
        } else {
            serviciosPage = servicioService.listarTodos(pageable);
        }
        
        // Pasar datos al modelo
        model.addAttribute("servicios", serviciosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", serviciosPage.getTotalPages());
        model.addAttribute("totalElements", serviciosPage.getTotalElements());
        model.addAttribute("size", size);
        
        // Usuario logueado
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        return "servicios";
    }
    
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login?required";
        }
        
        Optional<Servicio> servicioOpt = servicioService.buscarPorId(id);
        
        if (servicioOpt.isPresent()) {
            model.addAttribute("servicio", servicioOpt.get());
            return "servicio-detalle";
        }
        
        return "redirect:/servicios";
    }
}
