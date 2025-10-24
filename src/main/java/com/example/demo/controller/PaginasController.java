package com.example.demo.controller;

import com.example.demo.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginasController {
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "index";
    }
    
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "contact";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
