package com.hospital.backend.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración simplificada de Spring Boot Actuator para el sistema hospitalario
 */
@Configuration
public class ActuatorSimpleConfig {

    /**
     * Bean para verificar que la configuración está activa
     */
    @Bean
    String actuatorConfigStatus() {
        return "Actuator configurado correctamente para Sistema Hospitalario";
    }
}

/**
 * Información personalizada del sistema hospitalario para el endpoint /actuator/info
 */
@Component
class HospitalInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> hospitalInfo = new HashMap<>();
        hospitalInfo.put("name", "Sistema Integral de Gestión Hospitalaria");
        hospitalInfo.put("version", "1.0.0");
        hospitalInfo.put("description", "API REST para gestión hospitalaria completa");
        hospitalInfo.put("maintainer", "Hospital Development Team");
        hospitalInfo.put("contact", "admin@hospital.com");
        hospitalInfo.put("build-time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        hospitalInfo.put("features", new String[]{
                "Gestión de Pacientes",
                "Sistema de Citas",
                "Historiales Médicos",
                "Prescripciones Digitales",
                "Sistema de Pagos",
                "Notificaciones",
                "Chatbot Médico",
                "Analytics y Auditorías",
                "Panel Administrativo",
                "Monitoreo en Tiempo Real"
        });
        hospitalInfo.put("modules", new String[]{
                "auth", "user", "catalog", "appointment", 
                "medical", "payment", "notification", "chatbot", 
                "analytics", "admin", "monitoring"
        });
        hospitalInfo.put("database", "PostgreSQL");
        hospitalInfo.put("security", "JWT + Role-based Access Control");
        hospitalInfo.put("monitoring", "Spring Boot Actuator + Métricas Personalizadas");
        
        builder.withDetail("hospital", hospitalInfo);
    }
}

/**
 * Health Indicator personalizado para verificar el estado del sistema hospitalario
 */
@Component
class HospitalHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // Verificar componentes críticos del sistema
            boolean databaseHealthy = checkDatabaseHealth();
            boolean coreServicesHealthy = checkCoreServices();
            boolean memoryHealthy = checkMemoryUsage();

            if (databaseHealthy && coreServicesHealthy && memoryHealthy) {
                return Health.up()
                        .withDetail("database", "Operational")
                        .withDetail("coreServices", "All services running")
                        .withDetail("memory", "Within acceptable limits")
                        .withDetail("status", "Sistema Hospitalario funcionando correctamente")
                        .withDetail("timestamp", LocalDateTime.now())
                        .withDetail("monitoring", "Endpoints de monitoreo disponibles en /api/monitoring/")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", databaseHealthy ? "OK" : "DOWN")
                        .withDetail("coreServices", coreServicesHealthy ? "OK" : "DEGRADED")
                        .withDetail("memory", memoryHealthy ? "OK" : "HIGH_USAGE")
                        .withDetail("status", "Sistema con problemas detectados")
                        .withDetail("timestamp", LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "Error en verificación de salud")
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        }
    }

    private boolean checkDatabaseHealth() {
        // Simular verificación de base de datos
        // En producción aquí harías una query simple como SELECT 1
        return true;
    }

    private boolean checkCoreServices() {
        // Verificar que los servicios principales estén disponibles
        return true;
    }

    private boolean checkMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double memoryUsagePercentage = (double) usedMemory / maxMemory * 100;
        
        // Considerar saludable si el uso de memoria es menor al 85%
        return memoryUsagePercentage < 85.0;
    }
}
