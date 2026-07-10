package com.techsolutions.incidencias.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name = "incidencias")
@Data
public class Incidencia {
    // Agrega estos campos en tu clase Incidencia.java (si no los tienes) junto con sus Getters y Setters:
    private java.time.LocalDateTime fecha_registro;
    private java.time.LocalDateTime fecha_asignacion;
    private java.time.LocalDateTime fecha_inicio;
    private java.time.LocalDateTime fecha_fin;
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
    @ManyToOne
    @JoinColumn(name = "id_tecnico", referencedColumnName = "id_usuario", nullable = true)
    private Usuario tecnico; // O 'tecnico_asignado', verifica cómo se llama este atributo en tu Java // Representa al técnico asignado

    // No olvides agregar sus respectivos Getters y Setters

    @Column(updatable = false)
    private Date fecha_creacion = new Date();
}