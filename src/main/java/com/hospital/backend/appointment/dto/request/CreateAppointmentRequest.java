package com.hospital.backend.appointment.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para la creación de una nueva cita médica
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long patientId;

    @NotNull(message = "El ID del doctor es obligatorio")
    private Long doctorId;

    @NotNull(message = "El ID de la especialidad es obligatorio")
    private Long specialtyId;

    @NotNull(message = "La fecha de la cita es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser el día de hoy o posterior")
    private LocalDate appointmentDate;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    @NotBlank(message = "El motivo de la cita es obligatorio")
    @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
    private String reason;

    private Boolean inPerson = true;
    
    private String notes;
} 