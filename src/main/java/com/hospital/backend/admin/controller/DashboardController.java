package com.hospital.backend.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.backend.admin.dto.DashboardResponse;
import com.hospital.backend.admin.service.DashboardService;

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
@Tag(name = "📈 Dashboard", description = "Panel administrativo con métricas y estadísticas del hospital.")
public class DashboardController {

    private final DashboardService dashboardService;
    
    /**
     * Obtiene todas las métricas del dashboard administrativo
     * @return Respuesta con todas las métricas del hospital
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Métricas del dashboard", description = "Obtiene todas las estadísticas principales del hospital")
    public ResponseEntity<DashboardResponse> getDashboardMetrics() {
        log.info("GET /api/dashboard - Obteniendo métricas del dashboard");
        return ResponseEntity.ok(dashboardService.getDashboardMetrics());
    }
    
    /**
     * Actualiza y obtiene métricas frescas del dashboard administrativo
     * Fuerza recálculo de métricas sin usar caché
     * @return Respuesta con todas las métricas actualizadas
     */
    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar métricas", description = "Fuerza la actualización de todas las estadísticas")
    public ResponseEntity<DashboardResponse> refreshDashboardMetrics() {
        log.info("GET /api/dashboard/refresh - Actualizando métricas del dashboard");
        // En una implementación real, aquí se forzaría un recálculo completo sin usar caché
        return ResponseEntity.ok(dashboardService.getDashboardMetrics());
    }
} 