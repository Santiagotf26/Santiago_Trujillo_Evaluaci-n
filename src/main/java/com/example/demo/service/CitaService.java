package com.example.demo.service;

import com.example.demo.model.Cita;
import com.example.demo.model.Usuario;
import com.example.demo.model.Profesional;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CitaService {
    
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    
    public CitaService(CitaRepository citaRepository, UsuarioRepository usuarioRepository) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    public Cita guardar(Cita cita) {
        return citaRepository.save(cita);
    }
    
    public Cita actualizar(Cita cita) {
        return citaRepository.save(cita);
    }
    
    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }
    
    public Optional<Cita> buscarPorId(Long id) {
        return citaRepository.findById(id);
    }
    
    public List<Cita> listarPorUsuario(Usuario usuario) {
        return citaRepository.findByUsuario(usuario);
    }
    
    public List<Cita> buscarPorUsuarioId(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));
        return citaRepository.findByUsuario(usuario);
    }
    
    public List<Cita> listarPorUsuarioOrdenadas(Usuario usuario) {
        return citaRepository.findByUsuarioOrderByFechaHoraDesc(usuario);
    }

    public List<Cita> listarPorProfesional(Profesional profesional) {
        return citaRepository.findByProfesional(profesional);
    }

    public List<Cita> listarPorProfesionalOrdenadas(Profesional profesional) {
        return citaRepository.findByProfesionalOrderByFechaHoraDesc(profesional);
    }
    
    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }
    
    public boolean existeCita(Long id) {
        return citaRepository.existsById(id);
    }
}
