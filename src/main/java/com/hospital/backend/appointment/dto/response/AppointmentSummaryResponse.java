package com.hospital.backend.appointment.dto.response;

import com.hospital.backend.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO para la respuesta resumida de citas agrupadas por estado, especialidad, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSummaryResponse {

    private Long totalAppointments;
    private Map<String, Long> appointmentsByStatus; // Map<StatusName, Count>
    private Map<String, Long> appointmentsBySpecialty; // Map<SpecialtyName, Count>
    private Map<String, Double> cancellationRateByDoctor; // Map<DoctorName, CancellationRate>
    private Double noShowRate;
    private Double confirmationRate;
    private Double completionRate;
    
    private List<StatusCount> statusCounts;
    private List<SpecialtyCount> specialtyCounts;
    private List<DoctorCount> doctorCounts;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusCount {
        private AppointmentStatus status;
        private String statusName;
        private Long count;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialtyCount {
        private Long specialtyId;
        private String specialtyName;
        private Long count;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorCount {
        private Long doctorId;
        private String doctorName;
        private Long count;
        private Double percentage;
        private Long completedCount;
        private Long cancelledCount;
        private Long noShowCount;
    }
} 