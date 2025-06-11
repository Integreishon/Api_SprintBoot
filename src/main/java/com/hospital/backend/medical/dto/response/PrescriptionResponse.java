package com.hospital.backend.medical.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de una receta médica
 */
@Data
@Builder
public class PrescriptionResponse {

    private Long id;
    
    // Datos del paciente
    private Long patientId;
    private String patientName;
    
    // Datos del doctor
    private Long doctorId;
    private String doctorName;
    
    // Datos del registro médico
    private Long medicalRecordId;
    
    // Datos de la receta
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
    private String notes;
    private Integer quantity;
    private Integer refills;
    private Boolean active;
    
    // Información de dispensación
    private Boolean dispensed;
    private LocalDateTime dispensedDate;
    private String dispensedBy;
    
    // Metadatos
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
} 