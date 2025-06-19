package com.hospital.backend.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.backend.admin.dto.DashboardResponse;
import com.hospital.backend.admin.service.DashboardService;
import com.hospital.backend.common.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para el dashboard administrativo
 * Proporciona métricas y estadísticas del hospital
 */
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "📈 Dashboard Administrativo", description = "Panel de control con métricas, estadísticas y KPIs del hospital en tiempo real.")
public class DashboardController {

    private final DashboardService dashboardService;
    
    /**
     * Obtiene todas las métricas del dashboard administrativo
     * @return Respuesta con todas las métricas del hospital
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener métricas del dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardMetrics() {
        log.info("GET /api/admin/dashboard - Obteniendo métricas del dashboard");
        
        DashboardResponse metrics = dashboardService.getDashboardMetrics();
        
        return ResponseEntity.ok(
            ApiResponse.success("Métricas del dashboard obtenidas exitosamente", metrics)
        );
    }
    
    /**
     * Actualiza y obtiene métricas frescas del dashboard administrativo
     * @return Respuesta con todas las métricas actualizadas
     */
    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar métricas del dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> refreshDashboardMetrics() {
        log.info("GET /api/admin/dashboard/refresh - Actualizando métricas del dashboard");
        
        DashboardResponse metrics = dashboardService.getDashboardMetrics();
        
        return ResponseEntity.ok(
            ApiResponse.success("Métricas del dashboard actualizadas exitosamente", metrics)
        );
    }
}
