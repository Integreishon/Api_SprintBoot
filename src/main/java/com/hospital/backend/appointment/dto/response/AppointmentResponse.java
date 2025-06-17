package com.hospital.backend.appointment.dto.response;

import com.hospital.backend.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO de respuesta para mostrar información completa de una cita médica
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    
    private Long id;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime; // Calculado automáticamente
    private String reason;
    private BigDecimal price;
    private AppointmentStatus status;
    private String cancellationReason;
    private Long followUpAppointmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Información del paciente
    private PatientBasicInfo patient;
    
    // Información del doctor
    private DoctorBasicInfo doctor;
    
    // Información de la especialidad
    private SpecialtyBasicInfo specialty;
    
    // Clases internas para información básica
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientBasicInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String documentNumber;
        private String phone;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorBasicInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String cmpNumber;
        private String consultationRoom;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialtyBasicInfo {
        private Long id;
        private String name;
        private String description;
        private BigDecimal consultationPrice;
        private BigDecimal discountPercentage;
        private BigDecimal finalPrice;
        private Boolean isActive;
    }
}
