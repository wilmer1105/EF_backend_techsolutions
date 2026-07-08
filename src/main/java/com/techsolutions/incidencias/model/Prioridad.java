package com.techsolutions.incidencias.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "prioridades")
@Data
public class Prioridad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_prioridad;
    
    private String nombre_prioridad;
}