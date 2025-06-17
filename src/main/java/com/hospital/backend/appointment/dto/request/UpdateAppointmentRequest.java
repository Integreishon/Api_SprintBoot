package com.hospital.backend.appointment.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para actualizar una cita médica existente
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {
    
    @FutureOrPresent(message = "La fecha de la cita debe ser hoy o en el futuro")
    private LocalDate appointmentDate;
    
    private LocalTime startTime;
    
    @NotBlank(message = "El motivo de la consulta no puede estar vacío")
    private String reason;
    
    private String cancellationReason;
}
