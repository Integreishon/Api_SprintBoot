package com.hospital.backend.medical.dto.request;

import com.hospital.backend.enums.SeverityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la solicitud de creación de un nuevo registro médico
 * Adaptado a la nueva lógica de Urovital (sin prescripciones)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalRecordRequest {

    @NotNull(message = "El ID del doctor es obligatorio")
    private Long doctorId;

    @NotNull(message = "El ID de la cita es requerido")
    private Long appointmentId;

    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(min = 5, max = 1000, message = "El motivo de consulta debe tener entre 5 y 1000 caracteres")
    private String chiefComplaint;

    @Size(max = 2000, message = "Los síntomas no deben exceder los 2000 caracteres")
    private String symptoms;

    @NotBlank(message = "El diagnóstico es requerido")
    @Size(max = 2000, message = "El diagnóstico no debe exceder los 2000 caracteres")
    private String diagnosis;

    @NotBlank(message = "El tratamiento es requerido")
    @Size(max = 2000, message = "El plan de tratamiento no debe exceder los 2000 caracteres")
    private String treatment;

    @Size(max = 2000, message = "Las notas no deben exceder los 2000 caracteres")
    private String notes;

    private Boolean followupRequired = false;

    private LocalDateTime followupDate;

    private SeverityLevel severity;

    private Double heightCm;

    private Double weightKg;

    private String bloodPressure;

    private Double temperature;

    private Integer heartRate;

    private Integer respiratoryRate;

    private Integer oxygenSaturation;

    @Size(max = 1000, message = "Las alergias no deben exceder los 1000 caracteres")
    private String allergies;
} 