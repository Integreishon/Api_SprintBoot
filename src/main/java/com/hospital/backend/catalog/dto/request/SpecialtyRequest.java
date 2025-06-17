package com.hospital.backend.catalog.dto.request;

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
public class SpecialtyRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;
    
    private String description;
    
    @NotNull(message = "El precio de consulta es obligatorio")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private BigDecimal consultationPrice;
    
    @Digits(integer = 3, fraction = 2, message = "El descuento debe tener máximo 3 dígitos enteros y 2 decimales")
    @Min(value = 0, message = "El descuento no puede ser negativo")
    private BigDecimal discountPercentage;
    
    private Boolean isActive = true;
} 