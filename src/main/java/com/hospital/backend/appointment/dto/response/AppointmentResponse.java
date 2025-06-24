package com.hospital.backend.appointment.dto.response;

import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.enums.TimeBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para mostrar información completa de una cita médica
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long specialtyId;
    private String specialtyName;
    private LocalDate appointmentDate;
    private TimeBlock timeBlock;
    private String reason;
    private AppointmentStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
