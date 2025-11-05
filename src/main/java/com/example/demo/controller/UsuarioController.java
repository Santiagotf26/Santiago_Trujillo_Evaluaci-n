package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }
    
    // Procesar el registro del usuario
    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                    BindingResult result,
                                    Model model) {
        if (result.hasErrors()) {
            return "registro";
        }
        
        if (usuarioService.existeEmail(usuario.getEmail())) {
            model.addAttribute("error", "El email ya está registrado");
            return "registro";
        }
        
        usuarioService.registrar(usuario);
        return "redirect:/usuarios/registro?success";
    }
    
    // ✅ ACTUALIZADO: Procesar login con redirección según rol
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        // Buscar usuario por email en la base de datos
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar que la contraseña coincida
            if (usuario.getPassword().equals(password)) {
                // Login exitoso: Guardar usuario en la sesión
                session.setAttribute("usuarioLogueado", usuario);
                session.setAttribute("nombreUsuario", usuario.getNombre());
                session.setAttribute("emailUsuario", usuario.getEmail());
                session.setAttribute("rolUsuario", usuario.getRol());
                
                // ✅ Redirigir según el rol del usuario
                if (usuario.isAdmin()) {
                    // Si es ADMIN, redirigir al panel de administrador
                    return "redirect:/admin/dashboard";
                } else {
                    // Si es USER normal, redirigir a servicios
                    return "redirect:/servicios";
                }
            }
        }
        
        // Si el email no existe o la contraseña es incorrecta
        return "redirect:/login?error";
    }
    
    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
