package com.example.demo.api;

import com.example.demo.model.Servicio;
import com.example.demo.service.ServicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioRestController {
    
    private final ServicioService servicioService;
    
    public ServicioRestController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }
    
    // GET /api/servicios - Listar todos los servicios
    @GetMapping
    public ResponseEntity<List<Servicio>> listarTodos() {
        List<Servicio> servicios = servicioService.listarTodos();
        return ResponseEntity.ok(servicios);
    }
    
    // GET /api/servicios/{id} - Buscar servicio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Servicio> buscarPorId(@PathVariable Long id) {
        return servicioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/servicios - Crear nuevo servicio
    @PostMapping
    public ResponseEntity<Servicio> crear(@RequestBody Servicio servicio) {
        try {
            Servicio nuevoServicio = servicioService.guardar(servicio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PUT /api/servicios/{id} - Actualizar servicio existente
    @PutMapping("/{id}")
    public ResponseEntity<Servicio> actualizar(@PathVariable Long id, @RequestBody Servicio servicio) {
        return servicioService.buscarPorId(id)
                .map(servicioExistente -> {
                    servicio.setId(id);
                    Servicio actualizado = servicioService.actualizar(servicio);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // DELETE /api/servicios/{id} - Eliminar servicio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return servicioService.buscarPorId(id)
                .map(servicio -> {
                    servicioService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
