package com.techsolutions.incidencias.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estados")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_historial;

    @ManyToOne
    @JoinColumn(name = "id_incidencia", nullable = false)
    private Incidencia incidencia;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "id_tecnico", nullable = true)
    private Usuario tecnico;

    private LocalDateTime fecha_cambio;
    private String observacion;

    public HistorialEstado() {}

    public HistorialEstado(Incidencia incidencia, Estado estado, Usuario tecnico, String observacion) {
        this.incidencia = incidencia;
        this.estado = estado;
        this.tecnico = tecnico;
        this.fecha_cambio = LocalDateTime.now();
        this.observacion = observacion;
    }

    // REGIONES GETTERS Y SETTERS
    public Integer getId_historial() { return id_historial; }
    public void setId_historial(Integer id_historial) { this.id_historial = id_historial; }
    public Incidencia getIncidencia() { return incidencia; }
    public void setIncidencia(Incidencia incidencia) { this.incidencia = incidencia; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Usuario getTecnico() { return tecnico; }
    public void setTecnico(Usuario tecnico) { this.tecnico = tecnico; }
    public LocalDateTime getFecha_cambio() { return fecha_cambio; }
    public void setFecha_cambio(LocalDateTime fecha_cambio) { this.fecha_cambio = fecha_cambio; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}