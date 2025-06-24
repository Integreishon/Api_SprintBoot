package com.hospital.backend.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para solicitar la creación de un pago
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    
    @NotNull(message = "El ID de la cita es obligatorio")
    private Long appointmentId;
    
    @NotNull(message = "El ID del método de pago es obligatorio")
    private Long paymentMethodId;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal amount;
    
    private String transactionReference;
    
    private String payerName;
    
    private String payerEmail;
} 