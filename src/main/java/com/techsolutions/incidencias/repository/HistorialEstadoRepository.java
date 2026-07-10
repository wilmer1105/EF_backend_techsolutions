package com.techsolutions.incidencias.repository;

import com.techsolutions.incidencias.model.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Integer> {
    // Servirá para el reporte cronológico posterior
}