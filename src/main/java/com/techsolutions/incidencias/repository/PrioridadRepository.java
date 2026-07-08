package com.techsolutions.incidencias.repository;

import com.techsolutions.incidencias.model.Prioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrioridadRepository extends JpaRepository<Prioridad, Integer> {
    // JpaRepository maneja automáticamente las consultas básicas
}