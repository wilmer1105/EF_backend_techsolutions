package com.techsolutions.incidencias.repository;

import com.techsolutions.incidencias.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
    // Método para buscar todas las incidencias de un usuario específico
    // Cambiamos "IdUsuario" por "Id_usuario" para que coincida con el campo
    // Ahora sí puedes usar el formato camelCase correctamente
    List<Incidencia> findByUsuario_IdUsuario(Integer idUsuario);
}