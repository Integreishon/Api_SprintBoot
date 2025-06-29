package com.hospital.backend.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de rechazo de un pago pendiente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectPaymentRequest {
    
    @NotBlank(message = "El motivo del rechazo es obligatorio")
    @Size(min = 5, max = 200, message = "El motivo debe tener entre 5 y 200 caracteres")
    private String reason;
} 