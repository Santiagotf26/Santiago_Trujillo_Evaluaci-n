package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    // Guardar/actualizar usuario en la base de datos
    public Usuario registrar(Usuario usuario) {
        // Se guarda la contraseña tal cual llega (texto plano)
        return usuarioRepository.save(usuario);
    }
    
    // Actualizar usuario (igual que registrar, JPA detecta si existe por el ID)
    public Usuario actualizar(Usuario usuario) {
        // Se guarda la contraseña tal cual llega (texto plano)
        return usuarioRepository.save(usuario);
    }
    
    // Verificar si el email ya existe
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    // Buscar usuario por email
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    // Buscar usuario por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    // Eliminar usuario por ID
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
