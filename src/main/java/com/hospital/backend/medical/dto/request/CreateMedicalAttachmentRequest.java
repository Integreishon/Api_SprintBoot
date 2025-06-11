package com.hospital.backend.medical.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMedicalAttachmentRequest {
    
    @NotNull(message = "El ID del registro médico es requerido")
    private Long medicalRecordId;
    
    @NotBlank(message = "El nombre del archivo es requerido")
    private String fileName;
    
    @NotBlank(message = "El tipo de archivo es requerido")
    private String fileType;
    
    @NotNull(message = "El tamaño del archivo es requerido")
    private Long fileSize;
    
    @NotBlank(message = "La URL del archivo es requerida")
    private String fileUrl;
    
    private String description;
} 