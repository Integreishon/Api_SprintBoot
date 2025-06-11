package com.hospital.backend.catalog.dto.request;

import com.hospital.backend.enums.PaymentMethodType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;
    
    @NotNull(message = "El tipo de método de pago es obligatorio")
    private PaymentMethodType type;
    
    @Digits(integer = 3, fraction = 2, message = "La comisión debe tener máximo 3 dígitos enteros y 2 decimales")
    @Min(value = 0, message = "La comisión no puede ser negativa")
    private BigDecimal processingFee;
    
    private Boolean isActive = true;
    
    @Size(max = 50, message = "El código de integración debe tener máximo 50 caracteres")
    private String integrationCode;
} 