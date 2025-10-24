package com.example.demo.repository;

import com.example.demo.model.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    
    // Buscar servicios por nombre (búsqueda parcial)
    Page<Servicio> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
