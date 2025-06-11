package com.hospital.backend.medical.dto.response;

import com.hospital.backend.enums.SeverityLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la respuesta de un registro médico
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponse {

    private Long id;
    
    // Datos del paciente
    private Long patientId;
    private String patientName;
    private String patientDocument;
    private Integer patientAge;
    
    // Datos del doctor
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
    
    // Datos de la cita
    private Long appointmentId;
    private LocalDateTime appointmentDate;
    
    // Datos del registro médico
    private LocalDateTime recordDate;
    private String chiefComplaint;
    private String symptoms;
    private String diagnosis;
    private String treatmentPlan;
    private String notes;
    private Boolean followupRequired;
    private LocalDateTime followupDate;
    
    // Severidad
    private SeverityLevel severity;
    private String severityName;
    
    // Signos vitales
    private Double heightCm;
    private Double weightKg;
    private Double bmi; // Calculado
    private String bloodPressure;
    private Double temperature;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Integer oxygenSaturation;
    
    // Información adicional
    private String allergies;
    
    // Recetas y archivos adjuntos
    private List<PrescriptionResponse> prescriptions;
    private List<MedicalAttachmentResponse> attachments;
    
    // Metadatos
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
} 