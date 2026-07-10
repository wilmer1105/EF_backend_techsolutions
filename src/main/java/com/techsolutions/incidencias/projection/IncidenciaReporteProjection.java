package com.techsolutions.incidencias.projection;

import java.time.LocalDateTime;

public interface IncidenciaReporteProjection {
    Integer getId_incidencia();
    String getTitulo();
    String getDescripcion();
    LocalDateTime getFecha_registro();
    LocalDateTime getFecha_asignacion();
    LocalDateTime getFecha_inicio();
    LocalDateTime getFecha_fin();
    
    // Datos del Empleado que reportó
    String getEmpleado_nombre();
    
    // Categoría, Prioridad y Estado
    String getCategoria_nombre();
    String getPrioridad_nombre();
    String getEstado_nombre();
    
    // Datos del Técnico asignado
    String getTecnico_nombre();
}