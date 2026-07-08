package com.techsolutions.incidencias.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "asignaciones")
@Data
public class Asignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_asignacion;

    @ManyToOne
    @JoinColumn(name = "id_incidencia")
    private Incidencia incidencia;

    @ManyToOne
    @JoinColumn(name = "id_tecnico")
    private Usuario tecnico;

    private String observacion;

    @Column(updatable = false)
    private Date fecha_asignacion = new Date();
}