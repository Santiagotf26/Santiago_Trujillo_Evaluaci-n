package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.model.Profesional;
import com.example.demo.model.Servicio;
import com.example.demo.model.Cita;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.ProfesionalService;
import com.example.demo.service.ServicioService;
import com.example.demo.service.CitaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final UsuarioService usuarioService;
    private final ProfesionalService profesionalService;
    private final ServicioService servicioService;
    private final CitaService citaService;
    
    public AdminController(UsuarioService usuarioService, 
                          ProfesionalService profesionalService,
                          ServicioService servicioService,
                          CitaService citaService) {
        this.usuarioService = usuarioService;
        this.profesionalService = profesionalService;
        this.servicioService = servicioService;
        this.citaService = citaService;
    }
    
    // ========== DASHBOARD ==========
    
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model, HttpSession session) {
        Usuario admin = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (admin != null) {
            model.addAttribute("nombreAdmin", admin.getNombre());
        }
        
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("profesionales", profesionalService.listarTodos());
        model.addAttribute("servicios", servicioService.listarTodos());
        model.addAttribute("citas", citaService.listarTodas());
        
        return "admin/dashboard";
    }
    
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/usuarios";
    }
    
    @GetMapping("/profesionales")
    public String listarProfesionales(Model model) {
        model.addAttribute("profesionales", profesionalService.listarTodos());
        return "admin/profesionales";
    }
    
    @GetMapping("/servicios")
    public String listarServicios(Model model) {
        model.addAttribute("servicios", servicioService.listarTodos());
        return "admin/servicios";
    }
    
    @GetMapping("/citas")
    public String listarCitas(Model model) {
        model.addAttribute("citas", citaService.listarTodas());
        return "admin/citas";
    }
    
    // ========== EDICIÓN DE USUARIOS ==========
    
    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEditarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        model.addAttribute("usuario", usuario);
        return "admin/editar-usuario";
    }
    
    @PostMapping("/usuarios/actualizar")
    public String actualizarUsuario(@ModelAttribute("usuario") Usuario usuario, 
                                     RedirectAttributes redirectAttributes) {
        try {
            // Buscar el usuario existente en la base de datos
            Usuario usuarioExistente = usuarioService.buscarPorId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Actualizar solo los campos que vienen del formulario
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setEmail(usuario.getEmail());
            usuarioExistente.setTelefono(usuario.getTelefono());
            
            // Actualizar rol si viene en el formulario
            if (usuario.getRol() != null && !usuario.getRol().isEmpty()) {
                usuarioExistente.setRol(usuario.getRol());
            }
            
            // Solo actualizar la contraseña si no está vacía
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuarioExistente.setPassword(usuario.getPassword());
            }
            
            // Guardar en la base de datos
            usuarioService.actualizar(usuarioExistente);
            
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/admin/usuarios";
    }
    
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
            
            // Evitar que el admin se elimine a sí mismo
            if (usuarioLogueado != null && usuarioLogueado.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("mensaje", "No puedes eliminarte a ti mismo");
                redirectAttributes.addFlashAttribute("tipo", "warning");
                return "redirect:/admin/usuarios";
            }
            
            usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuarioService.eliminar(id);
            
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/admin/usuarios";
    }
    
    // ========== EDICIÓN DE PROFESIONALES ==========
    
    @GetMapping("/profesionales/editar/{id}")
    public String mostrarFormularioEditarProfesional(@PathVariable Long id, Model model) {
        Profesional profesional = profesionalService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Profesional no encontrado con id: " + id));
        
        model.addAttribute("profesional", profesional);
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/editar-profesional";
    }
    
    @PostMapping("/profesionales/actualizar")
    public String actualizarProfesional(@ModelAttribute("profesional") Profesional profesional,
                                        @RequestParam("usuarioId") Long usuarioId,
                                        @RequestParam(value = "horarioDisponible", required = false) String horarioDisponibleStr,
                                        RedirectAttributes redirectAttributes) {
        try {
            // Buscar el profesional existente
            Profesional profesionalExistente = profesionalService.buscarPorId(profesional.getId())
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
            
            // Buscar el usuario seleccionado
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Actualizar datos del profesional
            profesionalExistente.setEspecialidad(profesional.getEspecialidad());
            profesionalExistente.setUsuario(usuario);
            
            // Actualizar horario si viene del formulario
            if (horarioDisponibleStr != null && !horarioDisponibleStr.isEmpty()) {
                profesionalExistente.setHorarioDisponible(LocalDateTime.parse(horarioDisponibleStr));
            }
            
            // Guardar en la base de datos
            profesionalService.actualizar(profesionalExistente);
            
            redirectAttributes.addFlashAttribute("mensaje", "Profesional actualizado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar profesional: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/admin/profesionales";
    }
    
    @GetMapping("/profesionales/eliminar/{id}")
    public String eliminarProfesional(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            profesionalService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
            
            profesionalService.eliminar(id);
            
            redirectAttributes.addFlashAttribute("mensaje", "Profesional eliminado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar profesional: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/admin/profesionales";
    }
    
    // ========== EDICIÓN DE SERVICIOS ==========

    @GetMapping("/servicios/nuevo")
    public String mostrarFormularioNuevoServicio(Model model) {
        model.addAttribute("servicio", new Servicio());
        return "admin/nuevo-servicio";
    }

    @PostMapping("/servicios/nuevo")
    public String crearServicio(@ModelAttribute("servicio") Servicio servicio,
                                RedirectAttributes redirectAttributes) {
        try {
            servicioService.guardar(servicio);
            redirectAttributes.addFlashAttribute("mensaje", "Servicio creado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear servicio: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/admin/servicios";
    }

    @GetMapping("/servicios/editar/{id}")
    public String mostrarFormularioEditarServicio(@PathVariable Long id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: " + id));
        
        model.addAttribute("servicio", servicio);
        return "admin/editar-servicio";
    }

    @PostMapping("/servicios/actualizar")
    public String actualizarServicio(@ModelAttribute("servicio") Servicio servicio,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Buscar el servicio existente
            Servicio servicioExistente = servicioService.buscarPorId(servicio.getId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
            
            // Actualizar datos del servicio
            servicioExistente.setNombre(servicio.getNombre());
            servicioExistente.setDescripcion(servicio.getDescripcion());
            servicioExistente.setDuracion(servicio.getDuracion());
            servicioExistente.setPrecio(servicio.getPrecio());
            
            // Guardar en la base de datos
            servicioService.actualizar(servicioExistente);
            
            redirectAttributes.addFlashAttribute("mensaje", "Servicio actualizado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar servicio: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/admin/servicios";
    }

    @GetMapping("/servicios/eliminar/{id}")
    public String eliminarServicio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            servicioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
            
            servicioService.eliminar(id);
            
            redirectAttributes.addFlashAttribute("mensaje", "Servicio eliminado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar servicio: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/admin/servicios";
    }

    // ========== GESTIÓN DE CITAS ==========

    @GetMapping("/citas/editar/{id}")
    public String mostrarFormularioEditarCita(@PathVariable Long id, Model model) {
        Cita cita = citaService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + id));
        
        model.addAttribute("cita", cita);
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("servicios", servicioService.listarTodos());
        model.addAttribute("profesionales", profesionalService.listarTodos());
        return "admin/editar-cita";
    }

    @PostMapping("/citas/actualizar")
    public String actualizarCita(@ModelAttribute("cita") Cita cita,
                                 @RequestParam("usuarioId") Long usuarioId,
                                 @RequestParam("servicioId") Long servicioId,
                                 @RequestParam("profesionalId") Long profesionalId,
                                 @RequestParam(value = "fechaHora", required = false) String fechaHoraStr,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Buscar la cita existente
            Cita citaExistente = citaService.buscarPorId(cita.getId())
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            // Buscar las entidades relacionadas
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Servicio servicio = servicioService.buscarPorId(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
            Profesional profesional = profesionalService.buscarPorId(profesionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
            
            // Actualizar datos de la cita
            citaExistente.setUsuario(usuario);
            citaExistente.setServicio(servicio);
            citaExistente.setProfesional(profesional);
            citaExistente.setEstado(cita.getEstado());
            
            // Actualizar fecha y hora si viene del formulario
            if (fechaHoraStr != null && !fechaHoraStr.isEmpty()) {
                citaExistente.setFechaHora(LocalDateTime.parse(fechaHoraStr));
            }
            
            // Guardar en la base de datos
            citaService.actualizar(citaExistente);
            
            redirectAttributes.addFlashAttribute("mensaje", "Cita actualizada correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar cita: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/admin/citas";
    }

    @GetMapping("/citas/eliminar/{id}")
    public String eliminarCita(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            citaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            citaService.eliminar(id);
            
            redirectAttributes.addFlashAttribute("mensaje", "Cita eliminada correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar cita: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/admin/citas";
    }

    // ========== CONVERTIR USUARIO EN PROFESIONAL ==========

    @GetMapping("/usuarios/{id}/hacer-profesional")
    public String mostrarFormularioHacerProfesional(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        Profesional profesional = new Profesional();
        profesional.setUsuario(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("profesional", profesional);

        return "admin/hacer-profesional";
    }

    @PostMapping("/usuarios/{id}/hacer-profesional")
    public String hacerProfesional(@PathVariable Long id,
                                   @ModelAttribute("profesional") Profesional profesionalForm,
                                   @RequestParam(value = "horarioDisponible", required = false) String horarioDisponibleStr,
                                   RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Profesional profesional = new Profesional();
            profesional.setUsuario(usuario);
            profesional.setEspecialidad(profesionalForm.getEspecialidad());

            if (horarioDisponibleStr != null && !horarioDisponibleStr.isEmpty()) {
                profesional.setHorarioDisponible(LocalDateTime.parse(horarioDisponibleStr));
            }

            profesionalService.guardar(profesional);

            usuario.setRol("PROFESIONAL");
            usuarioService.actualizar(usuario);

            redirectAttributes.addFlashAttribute("mensaje", "Usuario convertido en profesional correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al convertir en profesional: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }

        return "redirect:/admin/usuarios";
    }
}
