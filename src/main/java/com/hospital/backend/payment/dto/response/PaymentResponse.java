package com.hospital.backend.payment.dto.response;

import com.hospital.backend.enums.PaymentMethodType;
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
 * DTO para respuestas de pagos
 * Adaptado para usar bloques de tiempo en lugar de horas específicas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private Long id;
    private Long appointmentId;
    private String patientName;
    private String doctorName;
    private String specialty;
    private LocalDate appointmentDate;
    private TimeBlock timeBlock;
    private String paymentMethodName;
    private PaymentMethodType paymentMethodType;
    private BigDecimal amount;
    private BigDecimal processingFee;
    private BigDecimal totalAmount;
    private String transactionReference;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String receiptNumber;
    private String payerName;
    private String payerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 