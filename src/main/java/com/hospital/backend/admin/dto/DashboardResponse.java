package com.hospital.backend.admin.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del dashboard administrativo
 * Contiene las métricas clave del sistema hospitalario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // Métricas generales del hospital
    private Long totalPatientsCount;
    private Long totalDoctorsCount;
    private Long totalAppointmentsCount;
    private Long pendingAppointmentsCount;
    private BigDecimal totalRevenue;
    
    // Métricas de hoy
    private Long todayAppointmentsCount;
    private Long todayPatientsCount;
    private BigDecimal todayRevenue;
    
    // Métricas semanales
    private Map<String, Long> appointmentsPerDay;
    private Map<String, BigDecimal> revenuePerDay;
    
    // Top especialidades
    private List<SpecialtyStats> topSpecialties;
    
    // Top doctores
    private List<DoctorStats> topDoctors;
    
    // Estadísticas de ocupación
    private BigDecimal appointmentOccupancyRate;
    private Long availableSlotsToday;
    
    /**
     * Clase interna para estadísticas de especialidades
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialtyStats {
        private Long specialtyId;
        private String specialtyName;
        private Long appointmentsCount;
        private BigDecimal revenue;
    }
    
    /**
     * Clase interna para estadísticas de doctores
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorStats {
        private Long doctorId;
        private String doctorName;
        private String specialtyName;
        private Long appointmentsCount;
        private BigDecimal revenue;
        private Double rating;
    }
} 