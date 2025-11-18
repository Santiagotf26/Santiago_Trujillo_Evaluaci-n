package com.example.demo.repository;

import com.example.demo.model.Cita;
import com.example.demo.model.Usuario;
import com.example.demo.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    
    // Buscar citas por usuario
    List<Cita> findByUsuario(Usuario usuario);
    
    // Buscar citas por usuario ordenadas por fecha descendente
    List<Cita> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);

    // Buscar citas por profesional
    List<Cita> findByProfesional(Profesional profesional);

    // Buscar citas por profesional ordenadas por fecha descendente
    List<Cita> findByProfesionalOrderByFechaHoraDesc(Profesional profesional);
}
