package com.techsolutions.incidencias.service;

import com.techsolutions.incidencias.projection.IncidenciaReporteProjection;
import com.techsolutions.incidencias.repository.IncidenciaRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell; // <- Importante para las celdas
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReporteService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    public void generarPdfGeneral(HttpServletResponse response) throws IOException {
        List<IncidenciaReporteProjection> registros = incidenciaRepository.obtenerReporteCompleto();
        construirPdf(registros, "REPORTE GENERAL DE INCIDENCIAS - TECHSOLUTIONS", response);
    }

    public void generarPdfPorEstado(Integer idEstado, HttpServletResponse response) throws IOException {
        List<IncidenciaReporteProjection> registros = incidenciaRepository.obtenerReportePorEstado(idEstado);
        String estadoNombre = (registros != null && !registros.isEmpty()) ? registros.get(0).getEstado_nombre() : String.valueOf(idEstado);
        construirPdf(registros, "REPORTE DE INCIDENCIAS - ESTADO: " + estadoNombre.toUpperCase(), response);
    }

    public void generarPdfPorTecnico(Integer idTecnico, HttpServletResponse response) throws IOException {
        List<IncidenciaReporteProjection> registros = incidenciaRepository.obtenerReportePorTecnico(idTecnico);
        String tecnicoNombre = (registros != null && !registros.isEmpty() && registros.get(0).getTecnico_nombre() != null)
                ? registros.get(0).getTecnico_nombre() : "Técnico #" + idTecnico;
        construirPdf(registros, "REPORTE DE ASIGNACIONES - TÉCNICO: " + tecnicoNombre.toUpperCase(), response);
    }

    public void generarPdfFiltrado(Integer idEstado, Integer idPrioridad, Integer idCategoria, Integer idTecnico,
                                    java.time.LocalDateTime fechaDesde, java.time.LocalDateTime fechaHasta,
                                    HttpServletResponse response) throws IOException {
        List<IncidenciaReporteProjection> registros = incidenciaRepository.obtenerReporteFiltrado(
                idEstado, idPrioridad, idCategoria, idTecnico, fechaDesde, fechaHasta);
        construirPdf(registros, "REPORTE DE RENDIMIENTO - INCIDENCIAS FILTRADAS", response);
    }
    private void construirPdf(List<IncidenciaReporteProjection> registros, String tituloReporte, HttpServletResponse response) throws IOException {
    Document document = new Document(PageSize.A4, 36, 36, 36, 36);
    PdfWriter.getInstance(document, response.getOutputStream());
    document.open();

    // Estilos de Fuente modernos
    Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, java.awt.Color.DARK_GRAY);
    Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, java.awt.Color.BLUE);
    Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, java.awt.Color.BLACK);
    Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 9, java.awt.Color.BLACK);

    // Título del PDF
    Paragraph titulo = new Paragraph(tituloReporte, fontTitulo);
    titulo.setAlignment(Element.ALIGN_CENTER);
    titulo.setSpacingAfter(20);
    document.add(titulo);

    if (registros == null || registros.isEmpty()) {
        Paragraph sinDatos = new Paragraph("No se encontraron incidencias con los criterios seleccionados.", fontBold);
        sinDatos.setAlignment(Element.ALIGN_CENTER);
        document.add(sinDatos);
        document.close();
        return;
    }

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    for (IncidenciaReporteProjection r : registros) {
        Paragraph headerTicket = new Paragraph("TICKET DE INCIDENCIA N°: " + r.getId_incidencia(), fontSubtitulo);
        headerTicket.setSpacingBefore(8);
        headerTicket.setSpacingAfter(4);
        document.add(headerTicket);

        // Tabla estructurada: 4 columnas fijas
        PdfPTable tableInfo = new PdfPTable(4);
        tableInfo.setWidthPercentage(100);
        
        // Configuración de anchos relativos libre de errores de casteo
        try {
            tableInfo.setWidths(new float[]{15f, 35f, 15f, 35f});
        } catch (Exception e) {
            // Bloque defensivo por si el motor demanda otra densidad
        }

        // Fila 1: Título y Categoría
        tableInfo.addCell(new PdfPCell(new Phrase("Asunto / Título:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getTitulo() != null ? r.getTitulo() : "N/A", fontNormal)));
        tableInfo.addCell(new PdfPCell(new Phrase("Categoría:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getCategoria_nombre() != null ? r.getCategoria_nombre() : "N/A", fontNormal)));

        // Fila 2: Solicitante y Técnico Asignado
        tableInfo.addCell(new PdfPCell(new Phrase("Reportado por:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getEmpleado_nombre() != null ? r.getEmpleado_nombre() : "N/A", fontNormal)));
        tableInfo.addCell(new PdfPCell(new Phrase("Técnico Asignado:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getTecnico_nombre() != null ? r.getTecnico_nombre() : "No Asignado", fontNormal)));

        // Fila 3: Prioridad y Estado Actual
        tableInfo.addCell(new PdfPCell(new Phrase("Prioridad / Gravedad:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getPrioridad_nombre() != null ? r.getPrioridad_nombre() : "N/A", fontNormal)));
        tableInfo.addCell(new PdfPCell(new Phrase("Estado Actual:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getEstado_nombre() != null ? r.getEstado_nombre() : "N/A", fontNormal)));

        // Fila 4: Línea de Tiempo completa del ciclo de vida
        tableInfo.addCell(new PdfPCell(new Phrase("Fecha Registro:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getFecha_registro() != null ? dtf.format(r.getFecha_registro()) : "N/A", fontNormal)));
        tableInfo.addCell(new PdfPCell(new Phrase("Fecha Asignación:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getFecha_asignacion() != null ? dtf.format(r.getFecha_asignacion()) : "Pendiente", fontNormal)));

        tableInfo.addCell(new PdfPCell(new Phrase("Inicio de Atención:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getFecha_inicio() != null ? dtf.format(r.getFecha_inicio()) : "Pendiente", fontNormal)));
        tableInfo.addCell(new PdfPCell(new Phrase("Fin de Atención:", fontBold)));
        tableInfo.addCell(new PdfPCell(new Phrase(r.getFecha_fin() != null ? dtf.format(r.getFecha_fin()) : "Pendiente", fontNormal)));

        document.add(tableInfo);

        // Detalle extendido de la incidencia
        Paragraph labelDesc = new Paragraph("Descripción del Problema:", fontBold);
        labelDesc.setSpacingBefore(5);
        document.add(labelDesc);
        
        Paragraph cuerpoDesc = new Paragraph(r.getDescripcion() != null ? r.getDescripcion() : "Sin descripción proveída.", fontNormal);
        cuerpoDesc.setSpacingAfter(5);
        document.add(cuerpoDesc);

        Paragraph separador = new Paragraph("_________________________________________________________________________________", fontNormal);
        separador.setSpacingAfter(10);
        document.add(separador);
    }

    document.close();
}
}