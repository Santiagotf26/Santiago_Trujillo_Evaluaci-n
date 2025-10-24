package com.example.demo.service;

import com.example.demo.model.Profesional;
import com.example.demo.repository.ProfesionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfesionalService {
    
    private final ProfesionalRepository profesionalRepository;
    
    public ProfesionalService(ProfesionalRepository profesionalRepository) {
        this.profesionalRepository = profesionalRepository;
    }
    
    // Listar todos los profesionales
    public List<Profesional> listarTodos() {
        return profesionalRepository.findAll();
    }
    
    // Buscar profesional por ID
    public Optional<Profesional> buscarPorId(Long id) {
        return profesionalRepository.findById(id);
    }
    
    // Buscar profesionales por especialidad
    public List<Profesional> buscarPorEspecialidad(String especialidad) {
        return profesionalRepository.findByEspecialidadContainingIgnoreCase(especialidad);
    }
    
    // Guardar profesional
    public Profesional guardar(Profesional profesional) {
        return profesionalRepository.save(profesional);
    }
    
    // Eliminar profesional
    public void eliminar(Long id) {
        profesionalRepository.deleteById(id);
    }
}
