package com.techsolutions.incidencias.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data // Lombok generará getIdUsuario() y setIdUsuario()
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // Esto vincula el campo con la columna SQL
    private Integer idUsuario; 
    
    private String nombres;
    private String apellidos;
    private String correo;
    private String password;
    private String estado;
    
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
}