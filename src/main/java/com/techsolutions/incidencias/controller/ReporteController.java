package com.techsolutions.incidencias.controller;

import com.techsolutions.incidencias.service.PdfReporteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReporteController {

    @Autowired
    private PdfReporteService pdfReporteService;

    @GetMapping("/incidencias/pdf")
    public void descargarPdfGeneral(HttpServletResponse response) throws IOException {
        try {
            configurarCabeceras(response, "Reporte_General_Incidencias");
            pdfReporteService.generarPdfGeneral(response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte PDF: " + e.getMessage());
        }
    }

    @GetMapping("/incidencias/estado/{idEstado}/pdf")
    public void descargarPdfPorEstado(@PathVariable Integer idEstado, HttpServletResponse response) throws IOException {
        try {
            configurarCabeceras(response, "Reporte_Incidencias_Estado_" + idEstado);
            pdfReporteService.generarPdfPorEstado(idEstado, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte por estado: " + e.getMessage());
        }
    }

    @GetMapping("/incidencias/tecnico/{idTecnico}/pdf")
    public void descargarPdfPorTecnico(@PathVariable Integer idTecnico, HttpServletResponse response) throws IOException {
        try {
            configurarCabeceras(response, "Reporte_Asignaciones_Tecnico_" + idTecnico);
            pdfReporteService.generarPdfPorTecnico(idTecnico, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte del técnico: " + e.getMessage());
        }
    }

    @GetMapping("/incidencias/filtro/pdf")
    public void descargarPdfFiltrado(
            @RequestParam(required = false) Integer idEstado,
            @RequestParam(required = false) Integer idPrioridad,
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) Integer idTecnico,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fechaDesde,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fechaHasta,
            HttpServletResponse response) throws IOException {
        try {
            configurarCabeceras(response, "Reporte_Rendimiento_Filtrado");
            java.time.LocalDateTime desde = fechaDesde != null ? fechaDesde.atStartOfDay() : null;
            java.time.LocalDateTime hasta = fechaHasta != null ? fechaHasta.atTime(23, 59, 59) : null;
            pdfReporteService.generarPdfFiltrado(idEstado, idPrioridad, idCategoria, idTecnico, desde, hasta, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte filtrado: " + e.getMessage());
        }
    }

    private void configurarCabeceras(HttpServletResponse response, String nombreArchivo) {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String fechaActual = dateFormatter.format(new Date());
        
        // Adjunto forzado para disparar la descarga directa en el navegador
        String headerValue = String.format("attachment; filename=%s_%s.pdf", nombreArchivo, fechaActual);
        response.setHeader("Content-Disposition", headerValue);
    }
}