package com.example.demo.security;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SpaUserDetailsService implements UserDetailsService {

    private final UsuarioService usuarioService;

    public SpaUserDetailsService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.buscarPorEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String role = usuario.getRol() != null ? usuario.getRol() : "USER";
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

        return new User(usuario.getEmail(), usuario.getPassword(), Collections.singleton(authority));
    }
}
