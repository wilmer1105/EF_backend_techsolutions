package com.techsolutions.incidencias.repository;

import com.techsolutions.incidencias.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {
    
    // Forzamos a Spring a usar SQL nativo ignorando las reglas de nombres automáticas
    @Query(value = "SELECT * FROM asignaciones WHERE id_incidencia = :idIncidencia", nativeQuery = true)
    Optional<Asignacion> findByIncidencia_Id_incidencia(@Param("idIncidencia") Integer idIncidencia);
    // Añade este método dentro de tu AsignacionRepository existente:

    @Query(value = "SELECT * FROM asignaciones WHERE id_tecnico = :idTecnico", nativeQuery = true)
    List<Asignacion> findByTecnicoId(@Param("idTecnico") Integer idTecnico);
}