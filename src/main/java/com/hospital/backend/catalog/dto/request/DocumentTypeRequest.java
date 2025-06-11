package com.hospital.backend.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeRequest {
    
    @NotBlank(message = "El código es obligatorio")
    @Size(min = 1, max = 10, message = "El código debe tener entre 1 y 10 caracteres")
    private String code;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;
    
    private String validationPattern;
    
    private Boolean isActive = true;
} 