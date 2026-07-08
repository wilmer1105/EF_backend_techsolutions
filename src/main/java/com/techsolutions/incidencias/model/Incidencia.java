package com.techsolutions.incidencias.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "incidencias")
@Data
public class Incidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_incidencia;
    
    private String titulo;
    private String descripcion;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "id_prioridad")
    private Prioridad prioridad;
    
    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
    
    @Column(updatable = false)
    private Date fecha_creacion = new Date();
}