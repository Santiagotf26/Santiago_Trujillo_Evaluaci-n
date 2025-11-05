package com.example.demo.service;

import com.example.demo.model.Servicio;
import com.example.demo.repository.ServicioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServicioService {
    
    private final ServicioRepository servicioRepository;
    
    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }
    
    // ✅ Listar todos los servicios SIN paginación
    public List<Servicio> listarTodos() {
        return servicioRepository.findAll();
    }
    
    // Listar servicios CON paginación
    public Page<Servicio> listarTodos(Pageable pageable) {
        return servicioRepository.findAll(pageable);
    }
    
    // Buscar servicios por nombre con paginación
    public Page<Servicio> buscarPorNombre(String nombre, Pageable pageable) {
        return servicioRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }
    
    // Buscar servicio por ID
    public Optional<Servicio> buscarPorId(Long id) {
        return servicioRepository.findById(id);
    }
    
    // ✅ AGREGAR: Guardar servicio (crear o actualizar)
    public Servicio guardar(Servicio servicio) {
        return servicioRepository.save(servicio);
    }
    
    // ✅ AGREGAR: Actualizar servicio (alias de guardar)
    public Servicio actualizar(Servicio servicio) {
        return servicioRepository.save(servicio);
    }
    
    // ✅ AGREGAR: Eliminar servicio por ID
    public void eliminar(Long id) {
        servicioRepository.deleteById(id);
    }
}
