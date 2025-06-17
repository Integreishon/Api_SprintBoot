package com.hospital.backend.appointment.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para crear una nueva cita médica
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    
    @NotNull(message = "El ID del paciente es obligatorio")
    private Long patientId;
    
    @NotNull(message = "El ID de la especialidad es obligatorio")
    private Long specialtyId;
    
    @NotNull(message = "El ID del doctor es obligatorio")
    private Long doctorId;
    
    @NotNull(message = "La fecha de la cita es obligatoria")
    @FutureOrPresent(message = "La fecha de la cita debe ser hoy o en el futuro")
    private LocalDate appointmentDate;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;
    
    @NotBlank(message = "El motivo de la consulta es obligatorio")
    private String reason;
}
