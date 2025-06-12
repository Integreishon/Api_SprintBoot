package com.hospital.backend.analytics.controller;

import com.hospital.backend.analytics.dto.AnalyticsRequest;
import com.hospital.backend.analytics.dto.AnalyticsResponse;
import com.hospital.backend.analytics.service.AnalyticsService;
import com.hospital.backend.analytics.service.ReportService;
import com.hospital.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para acceso a métricas y reportes analíticos
 */
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "📊 Analytics", description = "Análisis de datos y generación de reportes avanzados.")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ReportService reportService;
    
    /**
     * Obtiene métricas y gráficos según filtros
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generar análisis", description = "Genera métricas y gráficos personalizados según filtros")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics(
            @Valid @RequestBody AnalyticsRequest request) {
        AnalyticsResponse response = analyticsService.generateAnalytics(request);
        return ResponseEntity.ok(ApiResponse.success("Métricas generadas exitosamente", response));
    }
    
    /**
     * Exporta reporte en formato PDF
     */
    @PostMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exportar PDF", description = "Genera y descarga un reporte en formato PDF")
    public ResponseEntity<byte[]> exportPdfReport(@Valid @RequestBody AnalyticsRequest request) {
        byte[] reportBytes = reportService.generatePdfReport(request);
        
        String filename = String.format("hospital_report_%s_%s.pdf", 
                request.getStartDate(), request.getEndDate());
        
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(reportBytes.length)
                .body(reportBytes);
    }
    
    /**
     * Exporta reporte en formato Excel
     */
    @PostMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exportar Excel", description = "Genera y descarga un reporte en formato Excel")
    public ResponseEntity<byte[]> exportExcelReport(@Valid @RequestBody AnalyticsRequest request) {
        byte[] reportBytes = reportService.generateExcelReport(request);
        
        String filename = String.format("hospital_report_%s_%s.xlsx", 
                request.getStartDate(), request.getEndDate());
        
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(reportBytes.length)
                .body(reportBytes);
    }
    
    /**
     * Exporta reporte en formato CSV
     */
    @PostMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exportar CSV", description = "Genera y descarga un reporte en formato CSV")
    public ResponseEntity<byte[]> exportCsvReport(@Valid @RequestBody AnalyticsRequest request) {
        byte[] reportBytes = reportService.generateCsvReport(request);
        
        String filename = String.format("hospital_report_%s_%s.csv", 
                request.getStartDate(), request.getEndDate());
        
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(reportBytes.length)
                .body(reportBytes);
    }
    
    /**
     * Limpia el caché de métricas expirado
     */
    @PostMapping("/cache/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> cleanupCache() {
        int cleanedEntries = analyticsService.cleanupExpiredCache();
        return ResponseEntity.ok(ApiResponse.success("Caché limpiado exitosamente", cleanedEntries));
    }
    
    /**
     * Invalida el caché para una entidad específica
     */
    @PostMapping("/cache/invalidate/{entityName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> invalidateCache(@PathVariable String entityName) {
        int invalidatedEntries = analyticsService.invalidateCacheForEntity(entityName);
        return ResponseEntity.ok(ApiResponse.success("Caché invalidado para: " + entityName, invalidatedEntries));
    }
} 