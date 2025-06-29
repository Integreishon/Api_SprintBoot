package com.hospital.backend.payment.dto.response;

import com.hospital.backend.enums.TimeBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para representar una cita pendiente de validación en el portal de recepción
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingValidationResponse {
    
    private Long paymentId;
    private Long appointmentId;
    private String patientName;
    private String doctorName;
    private String specialtyName;
    private LocalDate appointmentDate;
    private TimeBlock timeBlock;
    private BigDecimal amount;
    private String paymentMethod;
    private String receiptImagePath;
    private LocalDateTime createdAt;
} 