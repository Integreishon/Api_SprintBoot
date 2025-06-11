package com.hospital.backend.analytics.service;

import com.hospital.backend.analytics.dto.AnalyticsRequest;
import com.hospital.backend.analytics.dto.AnalyticsResponse;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.enums.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Servicio para la generación de reportes en diferentes formatos
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final AnalyticsService analyticsService;
    private final AuditService auditService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    
    /**
     * Genera un reporte en formato PDF
     */
    public byte[] generatePdfReport(AnalyticsRequest request) {
        long startTime = System.currentTimeMillis();
        
        // Obtener datos analíticos
        AnalyticsResponse analyticsData = analyticsService.generateAnalytics(request);
        
        // Generar nombre del reporte
        String reportName = generateReportName(request, "pdf");
        
        try {
            // Aquí iría la implementación real de generación de PDF 
            // usando librerías como iText, JasperReports, etc.
            // Por ahora, simularemos la generación
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            simulatePdfGeneration(outputStream, analyticsData);
            
            // Registrar generación en auditoría
            long endTime = System.currentTimeMillis();
            auditService.logActionWithExecutionTime(
                    OperationType.EXPORT,
                    "Report",
                    null,
                    "Generación de reporte PDF: " + reportName,
                    endTime - startTime
            );
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            auditService.logError(
                    OperationType.EXPORT,
                    "Report",
                    null,
                    "Error generando reporte PDF: " + reportName,
                    e.getMessage()
            );
            throw new BusinessException("Error generando reporte PDF: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte en formato Excel
     */
    public byte[] generateExcelReport(AnalyticsRequest request) {
        long startTime = System.currentTimeMillis();
        
        // Obtener datos analíticos
        AnalyticsResponse analyticsData = analyticsService.generateAnalytics(request);
        
        // Generar nombre del reporte
        String reportName = generateReportName(request, "xlsx");
        
        try {
            // Aquí iría la implementación real de generación de Excel
            // usando librerías como Apache POI, etc.
            // Por ahora, simularemos la generación
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            simulateExcelGeneration(outputStream, analyticsData);
            
            // Registrar generación en auditoría
            long endTime = System.currentTimeMillis();
            auditService.logActionWithExecutionTime(
                    OperationType.EXPORT,
                    "Report",
                    null,
                    "Generación de reporte Excel: " + reportName,
                    endTime - startTime
            );
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            auditService.logError(
                    OperationType.EXPORT,
                    "Report",
                    null,
                    "Error generando reporte Excel: " + reportName,
                    e.getMessage()
            );
            throw new BusinessException("Error generando reporte Excel: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte en formato CSV
     */
    public byte[] generateCsvReport(AnalyticsRequest request) {
        long startTime = System.currentTimeMillis();
        
        // Obtener datos analíticos
        AnalyticsResponse analyticsData = analyticsService.generateAnalytics(request);
        
        // Generar nombre del reporte
        String reportName = generateReportName(request, "csv");
        
        try {
            // Generar CSV
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            generateCsvContent(outputStream, analyticsData);
            
            // Registrar generación en auditoría
            long endTime = System.currentTimeMillis();
            auditService.logActionWithExecutionTime(
                    OperationType.EXPORT,
                    "Report",
                    null,
                    "Generación de reporte CSV: " + reportName,
                    endTime - startTime
            );
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            auditService.logError(
                    OperationType.EXPORT,
                    "Report",
                    null,
                    "Error generando reporte CSV: " + reportName,
                    e.getMessage()
            );
            throw new BusinessException("Error generando reporte CSV: " + e.getMessage());
        }
    }
    
    /**
     * Genera un nombre para el reporte basado en parámetros y fecha
     */
    private String generateReportName(AnalyticsRequest request, String extension) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DATE_FORMATTER);
        String periodType = request.getPeriodType().name().toLowerCase();
        
        return String.format("hospital_report_%s_%s_%s_%s.%s",
                request.getStartDate(),
                request.getEndDate(),
                periodType,
                timestamp,
                extension);
    }
    
    /**
     * Simulación de generación de PDF (implementación real requeriría una librería PDF)
     */
    private void simulatePdfGeneration(ByteArrayOutputStream outputStream, AnalyticsResponse data) throws Exception {
        // Aquí iría el código real para generar PDF
        // En una implementación real, usaríamos iText, JasperReports, etc.
        // Por ahora, solo simulamos la generación
        outputStream.write("PDF REPORT CONTENT".getBytes());
        
        // Escribir métricas
        if (data.getMetrics() != null) {
            for (Map.Entry<String, Object> entry : data.getMetrics().entrySet()) {
                String line = entry.getKey() + ": " + entry.getValue() + "\n";
                outputStream.write(line.getBytes());
            }
        }
    }
    
    /**
     * Simulación de generación de Excel (implementación real requeriría Apache POI)
     */
    private void simulateExcelGeneration(ByteArrayOutputStream outputStream, AnalyticsResponse data) throws Exception {
        // Aquí iría el código real para generar Excel
        // En una implementación real, usaríamos Apache POI
        // Por ahora, solo simulamos la generación
        outputStream.write("EXCEL REPORT CONTENT".getBytes());
        
        // Escribir métricas
        if (data.getMetrics() != null) {
            for (Map.Entry<String, Object> entry : data.getMetrics().entrySet()) {
                String line = entry.getKey() + "\t" + entry.getValue() + "\n";
                outputStream.write(line.getBytes());
            }
        }
    }
    
    /**
     * Generación de contenido CSV
     */
    private void generateCsvContent(ByteArrayOutputStream outputStream, AnalyticsResponse data) throws Exception {
        // Cabecera
        outputStream.write("Métrica,Valor\n".getBytes());
        
        // Escribir métricas básicas
        outputStream.write(("Fecha Inicio," + data.getStartDate() + "\n").getBytes());
        outputStream.write(("Fecha Fin," + data.getEndDate() + "\n").getBytes());
        outputStream.write(("Tipo Período," + data.getPeriodType() + "\n").getBytes());
        
        // Escribir métricas detalladas
        if (data.getMetrics() != null) {
            for (Map.Entry<String, Object> entry : data.getMetrics().entrySet()) {
                if (entry.getValue() instanceof Map) {
                    // Para mapas anidados
                    @SuppressWarnings("unchecked")
                    Map<String, Object> nestedMap = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> nestedEntry : nestedMap.entrySet()) {
                        String line = entry.getKey() + " - " + nestedEntry.getKey() + "," + nestedEntry.getValue() + "\n";
                        outputStream.write(line.getBytes());
                    }
                } else {
                    // Para valores simples
                    String line = entry.getKey() + "," + entry.getValue() + "\n";
                    outputStream.write(line.getBytes());
                }
            }
        }
    }
} 