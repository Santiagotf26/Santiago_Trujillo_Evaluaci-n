package com.example.demo.repository;

import com.example.demo.model.Profesional;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {

    Profesional findByUsuario(Usuario usuario);
}
