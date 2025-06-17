package com.hospital.backend.medical.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para la solicitud de creación de una nueva receta médica
 */
@Data
public class CreatePrescriptionRequest {

    @NotNull(message = "El ID del registro médico es requerido")
    private Long medicalRecordId;
    
    @NotBlank(message = "El nombre del medicamento es requerido")
    private String medicationName;
    
    @NotBlank(message = "La dosis es requerida")
    private String dosage;
    
    @NotBlank(message = "La frecuencia es requerida")
    private String frequency;
    
    @NotBlank(message = "La duración es requerida")
    private String duration;
    
    private String instructions;
} 