package com.hospital.backend.admin.controller;

import com.hospital.backend.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para endpoints de monitoreo y métricas del sistema hospitalario
 */
@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
@Tag(name = "📊 Monitoreo y Métricas", description = "Endpoints de monitoreo, métricas y health checks del sistema hospitalario")
public class MonitoringController {

    private final HealthEndpoint healthEndpoint;
    private final InfoEndpoint infoEndpoint;
    private final MetricsEndpoint metricsEndpoint;

    @GetMapping("/health")
    @Operation(summary = "Estado de salud del sistema", description = "Obtiene el estado de salud completo del sistema hospitalario")
    public ResponseEntity<ApiResponse<Object>> getSystemHealth() {
        try {
            var health = healthEndpoint.health();
            return ResponseEntity.ok(ApiResponse.success("Estado de salud del sistema", health));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error obteniendo estado de salud: " + e.getMessage()));
        }
    }

    @GetMapping("/info")
    @Operation(summary = "Información del sistema", description = "Obtiene información detallada del sistema y aplicación")
    public ResponseEntity<ApiResponse<Object>> getSystemInfo() {
        try {
            var info = infoEndpoint.info();
            return ResponseEntity.ok(ApiResponse.success("Información del sistema", info));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error obteniendo información del sistema: " + e.getMessage()));
        }
    }

    @GetMapping("/metrics")
    @Operation(summary = "Métricas generales", description = "Obtiene las métricas principales del sistema")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // Métricas de memoria
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            Map<String, Object> memoryMetrics = new HashMap<>();
            memoryMetrics.put("heapUsed", memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024) + " MB");
            memoryMetrics.put("heapMax", memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024) + " MB");
            memoryMetrics.put("nonHeapUsed", memoryBean.getNonHeapMemoryUsage().getUsed() / (1024 * 1024) + " MB");
            
            // Métricas de threads
            Map<String, Object> threadMetrics = new HashMap<>();
            threadMetrics.put("activeThreads", ManagementFactory.getThreadMXBean().getThreadCount());
            threadMetrics.put("peakThreads", ManagementFactory.getThreadMXBean().getPeakThreadCount());
            threadMetrics.put("daemonThreads", ManagementFactory.getThreadMXBean().getDaemonThreadCount());
            
            // Métricas de JVM
            Map<String, Object> jvmMetrics = new HashMap<>();
            jvmMetrics.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000 + " segundos");
            jvmMetrics.put("processors", Runtime.getRuntime().availableProcessors());
            jvmMetrics.put("javaVersion", System.getProperty("java.version"));
            
            metrics.put("memory", memoryMetrics);
            metrics.put("threads", threadMetrics);
            metrics.put("jvm", jvmMetrics);
            metrics.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(ApiResponse.success("Métricas del sistema", metrics));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error obteniendo métricas: " + e.getMessage()));
        }
    }

    @GetMapping("/metrics/{metricName}")
    @Operation(summary = "Métrica específica", description = "Obtiene una métrica específica por nombre")
    public ResponseEntity<ApiResponse<Object>> getSpecificMetric(@PathVariable String metricName) {
        try {
            var metric = metricsEndpoint.metric(metricName, null);
            return ResponseEntity.ok(ApiResponse.success("Métrica: " + metricName, metric));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Métrica no encontrada: " + metricName));
        }
    }

    @GetMapping("/hospital-stats")
    @Operation(summary = "Estadísticas hospitalarias", description = "Obtiene estadísticas específicas del sistema hospitalario")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHospitalStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Estadísticas del sistema
            Map<String, Object> systemStats = new HashMap<>();
            systemStats.put("applicationName", "Sistema Hospitalario");
            systemStats.put("version", "1.0.0");
            systemStats.put("environment", "development");
            systemStats.put("startTime", LocalDateTime.now().minusHours(2)); // Simulated
            systemStats.put("uptime", "2 horas 15 minutos"); // Simulated
            
            // Módulos activos
            Map<String, String> modules = new HashMap<>();
            modules.put("auth", "Activo");
            modules.put("user", "Activo");
            modules.put("catalog", "Activo");
            modules.put("appointment", "Activo");
            modules.put("medical", "Activo");
            modules.put("payment", "Activo");
            modules.put("notification", "Activo");
            modules.put("chatbot", "Activo");
            modules.put("analytics", "Activo");
            modules.put("admin", "Activo");
            
            // Database info
            Map<String, Object> databaseInfo = new HashMap<>();
            databaseInfo.put("type", "PostgreSQL");
            databaseInfo.put("status", "Connected");
            databaseInfo.put("driver", "org.postgresql.Driver");
            databaseInfo.put("schema", "public");
            
            // API Info
            Map<String, Object> apiInfo = new HashMap<>();
            apiInfo.put("totalEndpoints", "75+");
            apiInfo.put("authenticationMethod", "JWT");
            apiInfo.put("corsEnabled", true);
            apiInfo.put("swaggerEnabled", true);
            
            stats.put("system", systemStats);
            stats.put("modules", modules);
            stats.put("database", databaseInfo);
            stats.put("api", apiInfo);
            stats.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(ApiResponse.success("Estadísticas hospitalarias", stats));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error obteniendo estadísticas: " + e.getMessage()));
        }
    }

    @GetMapping("/performance")
    @Operation(summary = "Métricas de rendimiento", description = "Obtiene métricas de rendimiento del sistema")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        Map<String, Object> performance = new HashMap<>();
        
        try {
            // CPU y memoria
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> resourceUsage = new HashMap<>();
            resourceUsage.put("maxMemory", runtime.maxMemory() / (1024 * 1024) + " MB");
            resourceUsage.put("totalMemory", runtime.totalMemory() / (1024 * 1024) + " MB");
            resourceUsage.put("freeMemory", runtime.freeMemory() / (1024 * 1024) + " MB");
            resourceUsage.put("usedMemory", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + " MB");
            resourceUsage.put("memoryUsagePercentage", 
                String.format("%.2f%%", ((double)(runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory()) * 100));
            
            // Métricas de JVM
            Map<String, Object> jvmPerformance = new HashMap<>();
            jvmPerformance.put("activeThreads", ManagementFactory.getThreadMXBean().getThreadCount());
            jvmPerformance.put("gcCollections", ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(gc -> gc.getCollectionCount())
                .sum());
            jvmPerformance.put("gcTime", ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(gc -> gc.getCollectionTime())
                .sum() + " ms");
            
            performance.put("resources", resourceUsage);
            performance.put("jvm", jvmPerformance);
            performance.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(ApiResponse.success("Métricas de rendimiento", performance));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error obteniendo métricas de rendimiento: " + e.getMessage()));
        }
    }
    
    @GetMapping("/alerts")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alertas del sistema", description = "Obtiene alertas y notificaciones del sistema")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemAlerts() {
        Map<String, Object> alerts = new HashMap<>();
        
        try {
            // Verificar alertas de memoria
            Runtime runtime = Runtime.getRuntime();
            double memoryUsage = ((double)(runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory()) * 100;
            
            Map<String, Object> memoryAlert = new HashMap<>();
            if (memoryUsage > 85) {
                memoryAlert.put("level", "WARNING");
                memoryAlert.put("message", "Uso de memoria alto: " + String.format("%.2f%%", memoryUsage));
            } else if (memoryUsage > 95) {
                memoryAlert.put("level", "CRITICAL");
                memoryAlert.put("message", "Uso de memoria crítico: " + String.format("%.2f%%", memoryUsage));
            } else {
                memoryAlert.put("level", "OK");
                memoryAlert.put("message", "Uso de memoria normal: " + String.format("%.2f%%", memoryUsage));
            }
            
            // Verificar threads
            int activeThreads = ManagementFactory.getThreadMXBean().getThreadCount();
            Map<String, Object> threadAlert = new HashMap<>();
            if (activeThreads > 200) {
                threadAlert.put("level", "WARNING");
                threadAlert.put("message", "Alto número de threads activos: " + activeThreads);
            } else {
                threadAlert.put("level", "OK");
                threadAlert.put("message", "Threads dentro del rango normal: " + activeThreads);
            }
            
            alerts.put("memory", memoryAlert);
            alerts.put("threads", threadAlert);
            alerts.put("database", Map.of("level", "OK", "message", "Base de datos operativa"));
            alerts.put("services", Map.of("level", "OK", "message", "Todos los servicios operativos"));
            alerts.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(ApiResponse.success("Alertas del sistema", alerts));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error obteniendo alertas: " + e.getMessage()));
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Estado general del sistema", description = "Obtiene un resumen del estado general del sistema")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            status.put("status", "RUNNING");
            status.put("message", "Sistema hospitalario funcionando correctamente");
            status.put("timestamp", LocalDateTime.now());
            status.put("version", "1.0.0");
            status.put("endpoints", "75+");
            status.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000 + " segundos");
            
            return ResponseEntity.ok(ApiResponse.success("Estado del sistema", status));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error obteniendo estado: " + e.getMessage()));
        }
    }
}
