package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "profesional")
public class Profesional {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String especialidad;
    
    @Column(name = "horario_disponible")
    private LocalDateTime horarioDisponible;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    // Constructores
    public Profesional() {}
    
    public Profesional(String especialidad, LocalDateTime horarioDisponible) {
        this.especialidad = especialidad;
        this.horarioDisponible = horarioDisponible;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    public LocalDateTime getHorarioDisponible() {
        return horarioDisponible;
    }
    
    public void setHorarioDisponible(LocalDateTime horarioDisponible) {
        this.horarioDisponible = horarioDisponible;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    @Override
    public String toString() {
        return "Profesional{" +
                "id=" + id +
                ", especialidad='" + especialidad + '\'' +
                ", horarioDisponible=" + horarioDisponible +
                '}';
    }
}
