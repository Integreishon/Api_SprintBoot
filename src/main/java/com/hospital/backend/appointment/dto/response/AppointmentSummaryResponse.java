package com.hospital.backend.appointment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para resúmenes y estadísticas de citas
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSummaryResponse {
    
    private Long totalAppointments;
    private Long scheduledCount;
    private Long confirmedCount;
    private Long completedCount;
    private Long cancelledCount;
    private Long noShowCount;
    private BigDecimal totalRevenue;
    private LocalDate reportDate;
    
    // Constructor para consultas de base de datos
    public AppointmentSummaryResponse(Long totalAppointments, BigDecimal totalRevenue) {
        this.totalAppointments = totalAppointments;
        this.totalRevenue = totalRevenue;
    }
}
