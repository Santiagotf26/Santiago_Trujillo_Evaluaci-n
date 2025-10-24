package com.example.demo.service;

import com.example.demo.model.Servicio;
import com.example.demo.repository.ServicioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class ServicioService {
    
    private final ServicioRepository servicioRepository;
    
    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }
    
    // Listar servicios con paginación
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
}
