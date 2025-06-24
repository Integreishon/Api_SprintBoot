package com.hospital.backend.appointment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para resúmenes y estadísticas de citas
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSummaryResponse {
    
    private Long totalAppointments;
    private Long scheduledCount;
    private Long inProgressCount;
    private Long completedCount;
    private Long cancelledCount;
    private Long noShowCount;
    private BigDecimal totalRevenue;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate reportDate;
    
    // Constructor para consultas de base de datos
    public AppointmentSummaryResponse(Long totalAppointments, BigDecimal totalRevenue) {
        this.totalAppointments = totalAppointments;
        this.totalRevenue = totalRevenue;
    }
    
    /**
     * Builder personalizado para establecer contadores de citas por estado
     */
    public static class AppointmentSummaryResponseBuilder {
        public AppointmentSummaryResponseBuilder completedAppointments(long count) {
            this.completedCount = count;
            return this;
        }
        
        public AppointmentSummaryResponseBuilder cancelledAppointments(long count) {
            this.cancelledCount = count;
            return this;
        }
        
        public AppointmentSummaryResponseBuilder noShowAppointments(long count) {
            this.noShowCount = count;
            return this;
        }
        
        public AppointmentSummaryResponseBuilder scheduledAppointments(long count) {
            this.scheduledCount = count;
            return this;
        }
        
        public AppointmentSummaryResponseBuilder inConsultationAppointments(long count) {
            this.inProgressCount = count;
            return this;
        }
    }
}
