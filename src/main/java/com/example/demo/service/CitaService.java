package com.example.demo.service;

import com.example.demo.model.Cita;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CitaService {
    
    private final CitaRepository citaRepository;
    
    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }
    
    public Cita guardar(Cita cita) {
        return citaRepository.save(cita);
    }
    
    public List<Cita> listarPorUsuario(Usuario usuario) {
        return citaRepository.findByUsuario(usuario);
    }
    
    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }
}
