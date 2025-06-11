package com.hospital.backend.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la creación de un nuevo pago
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "El ID de la cita es obligatorio")
    private Long appointmentId;
    
    @NotNull(message = "El método de pago es obligatorio")
    private Long paymentMethodId;
    
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal amount;
    
    private String payerName;
    
    private String payerEmail;
    
    private String notes;
    
    private String transactionReference;
} 