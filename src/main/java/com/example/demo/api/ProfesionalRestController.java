package com.example.demo.api;

import com.example.demo.model.Profesional;
import com.example.demo.service.ProfesionalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesionales")
@CrossOrigin(origins = "*")
public class ProfesionalRestController {
    
    private final ProfesionalService profesionalService;
    
    public ProfesionalRestController(ProfesionalService profesionalService) {
        this.profesionalService = profesionalService;
    }
    
    // GET /api/profesionales - Listar todos los profesionales
    @GetMapping
    public ResponseEntity<List<Profesional>> listarTodos() {
        List<Profesional> profesionales = profesionalService.listarTodos();
        return ResponseEntity.ok(profesionales);
    }
    
    // GET /api/profesionales/{id} - Buscar profesional por ID
    @GetMapping("/{id}")
    public ResponseEntity<Profesional> buscarPorId(@PathVariable Long id) {
        return profesionalService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/profesionales - Crear nuevo profesional
    @PostMapping
    public ResponseEntity<Profesional> crear(@RequestBody Profesional profesional) {
        try {
            Profesional nuevoProfesional = profesionalService.guardar(profesional);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProfesional);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PUT /api/profesionales/{id} - Actualizar profesional existente
    @PutMapping("/{id}")
    public ResponseEntity<Profesional> actualizar(@PathVariable Long id, @RequestBody Profesional profesional) {
        return profesionalService.buscarPorId(id)
                .map(profesionalExistente -> {
                    profesional.setId(id);
                    Profesional actualizado = profesionalService.actualizar(profesional);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // DELETE /api/profesionales/{id} - Eliminar profesional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return profesionalService.buscarPorId(id)
                .map(profesional -> {
                    profesionalService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
