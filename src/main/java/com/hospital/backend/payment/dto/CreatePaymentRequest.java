package com.hospital.backend.payment.dto;

import jakarta.validation.constraints.Email;
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
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal amount;
    
    private String payerName;
    
    @Email(message = "El email debe tener un formato válido")
    private String payerEmail;
    
    private String transactionReference;
}
