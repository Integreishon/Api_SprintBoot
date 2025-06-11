package com.hospital.backend.appointment.dto.response;

import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para la respuesta de información detallada de una cita médica
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private Long id;

    // Datos del paciente
    private Long patientId;
    private String patientName;
    private String patientDocument;
    private String patientPhone;
    private String patientEmail;

    // Datos del doctor
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private String doctorOfficeNumber;
    private String doctorPhone;
    private String doctorEmail;

    // Datos de la especialidad
    private Long specialtyId;
    private String specialtyName;
    private Integer averageDuration;

    // Datos de la cita
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private String notes;
    private BigDecimal price;
    
    // Estados
    private AppointmentStatus status;
    private String statusName;
    private PaymentStatus paymentStatus;
    private String paymentStatusName;
    
    // Tipo de cita
    private Boolean inPerson;
    private String virtualMeetingUrl;
    
    // Información adicional
    private Boolean reminderSent;
    private Long followUpAppointmentId;
    private String cancellationReason;
    
    // Metadatos y auditoría
    private LocalDate createdAt;
    private String createdBy;
} 