package com.example.demo.service;

import com.example.demo.model.Profesional;
import com.example.demo.model.Usuario;
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
    
    // Guardar/actualizar profesional (JPA detecta si es nuevo o actualización)
    public Profesional guardar(Profesional profesional) {
        return profesionalRepository.save(profesional);
    }
    
    // Actualizar profesional (alias del método guardar)
    public Profesional actualizar(Profesional profesional) {
        return profesionalRepository.save(profesional);
    }
    
    // Eliminar profesional por ID
    public void eliminar(Long id) {
        profesionalRepository.deleteById(id);
    }

    // Buscar profesional a partir del usuario asociado
    public Profesional buscarPorUsuario(Usuario usuario) {
        return profesionalRepository.findByUsuario(usuario);
    }
}
