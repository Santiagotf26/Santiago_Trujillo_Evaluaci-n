package com.example.demo.repository;

import com.example.demo.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    
    // Buscar profesionales por especialidad
    List<Profesional> findByEspecialidadContainingIgnoreCase(String especialidad);
}
