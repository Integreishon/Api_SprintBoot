package com.hospital.backend.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.backend.enums.PaymentMethodType;
import com.hospital.backend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO para la respuesta de información de pagos
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;
    
    private String paymentMethodName;
    
    private PaymentMethodType paymentMethodType;
    
    private BigDecimal amount;
    
    private BigDecimal processingFee;
    
    private BigDecimal totalAmount;
    
    private String transactionReference;
    
    private PaymentStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;
    
    private String receiptNumber;
    
    private String payerName;
    
    private String payerEmail;
    
    private String notes;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
} 