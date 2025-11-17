package com.example.demo.security;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioService usuarioService;

    public CustomAuthenticationSuccessHandler(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();

        usuarioService.buscarPorEmail(email).ifPresent(usuario -> {
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("nombreUsuario", usuario.getNombre());
            session.setAttribute("emailUsuario", usuario.getEmail());
            session.setAttribute("rolUsuario", usuario.getRol());
        });

        String redirectUrl = "/servicios";

        if (usuarioService.buscarPorEmail(email).map(Usuario::isAdmin).orElse(false)) {
            redirectUrl = "/admin/dashboard";
        }

        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}
