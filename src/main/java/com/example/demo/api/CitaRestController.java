package com.example.demo.api;

import com.example.demo.model.Cita;
import com.example.demo.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaRestController {
    
    private final CitaService citaService;
    
    public CitaRestController(CitaService citaService) {
        this.citaService = citaService;
    }
    
    // GET /api/citas - Listar todas las citas
    @GetMapping
    public ResponseEntity<List<Cita>> listarTodas() {
        List<Cita> citas = citaService.listarTodas();
        return ResponseEntity.ok(citas);
    }
    
    // GET /api/citas/{id} - Buscar cita por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cita> buscarPorId(@PathVariable Long id) {
        return citaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/citas - Crear nueva cita
    @PostMapping
    public ResponseEntity<Cita> crear(@RequestBody Cita cita) {
        try {
            Cita nuevaCita = citaService.guardar(cita);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCita);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PUT /api/citas/{id} - Actualizar cita existente
    @PutMapping("/{id}")
    public ResponseEntity<Cita> actualizar(@PathVariable Long id, @RequestBody Cita cita) {
        return citaService.buscarPorId(id)
                .map(citaExistente -> {
                    cita.setId(id);
                    Cita actualizada = citaService.guardar(cita);
                    return ResponseEntity.ok(actualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // DELETE /api/citas/{id} - Eliminar cita
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return citaService.buscarPorId(id)
                .map(cita -> {
                    citaService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET /api/citas/usuario/{usuarioId} - Buscar citas por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Cita>> buscarPorUsuario(@PathVariable Long usuarioId) {
        List<Cita> citas = citaService.buscarPorUsuarioId(usuarioId);
        return ResponseEntity.ok(citas);
    }
}
