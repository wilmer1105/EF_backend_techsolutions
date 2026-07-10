package com.techsolutions.incidencias.repository;

import com.techsolutions.incidencias.model.Incidencia;
import com.techsolutions.incidencias.projection.IncidenciaReporteProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {

    // Búsqueda común para Angular que ya tenías
    List<Incidencia> findByUsuario_IdUsuario(Integer idUsuario);

    // Asignaciones reales del técnico: la fuente de verdad es incidencias.id_tecnico
    List<Incidencia> findByTecnico_IdUsuario(Integer idTecnico);

    // ESTRUCTURA BASE DE LA QUERY DE REPORTES (Unión relacional optimizada)
    String QUERY_REPORTE = "SELECT " +
            "i.id_incidencia AS id_incidencia, i.titulo AS titulo, i.descripcion AS descripcion, " +
            "i.fecha_registro AS fecha_registro, i.fecha_asignacion AS fecha_asignacion, " +
            "i.fecha_inicio AS fecha_inicio, i.fecha_fin AS fecha_fin, " +
            "u_emp.nombres AS empleado_nombre, " +
            "c.nombre_categoria AS categoria_nombre, " +
            "p.nombre_prioridad AS prioridad_nombre, " +
            "e.nombre_estado AS estado_nombre, " +
            "u_tec.nombres AS tecnico_nombre " +
            "FROM incidencias i " +
            "LEFT JOIN usuarios u_emp ON i.id_usuario = u_emp.id_usuario " +
            "LEFT JOIN categorias c ON i.id_categoria = c.id_categoria " +
            "LEFT JOIN prioridades p ON i.id_prioridad = p.id_prioridad " +
            "LEFT JOIN estados e ON i.id_estado = e.id_estado " +
            "LEFT JOIN usuarios u_tec ON i.id_tecnico = u_tec.id_usuario";

    @Query(value = QUERY_REPORTE, nativeQuery = true)
    List<IncidenciaReporteProjection> obtenerReporteCompleto();

    @Query(value = QUERY_REPORTE + " WHERE i.id_estado = :idEstado", nativeQuery = true)
    List<IncidenciaReporteProjection> obtenerReportePorEstado(@Param("idEstado") Integer idEstado);

    @Query(value = QUERY_REPORTE + " WHERE i.id_tecnico = :idTecnico", nativeQuery = true)
    List<IncidenciaReporteProjection> obtenerReportePorTecnico(@Param("idTecnico") Integer idTecnico);

    @Query(value = QUERY_REPORTE +
            " WHERE (:idEstado IS NULL OR i.id_estado = :idEstado) " +
            " AND (:idPrioridad IS NULL OR i.id_prioridad = :idPrioridad) " +
            " AND (:idCategoria IS NULL OR i.id_categoria = :idCategoria) " +
            " AND (:idTecnico IS NULL OR i.id_tecnico = :idTecnico) " +
            " AND (:fechaDesde IS NULL OR i.fecha_registro >= :fechaDesde) " +
            " AND (:fechaHasta IS NULL OR i.fecha_registro <= :fechaHasta)",
            nativeQuery = true)
    List<IncidenciaReporteProjection> obtenerReporteFiltrado(
            @Param("idEstado") Integer idEstado,
            @Param("idPrioridad") Integer idPrioridad,
            @Param("idCategoria") Integer idCategoria,
            @Param("idTecnico") Integer idTecnico,
            @Param("fechaDesde") java.time.LocalDateTime fechaDesde,
            @Param("fechaHasta") java.time.LocalDateTime fechaHasta);
}