package com.hospital.backend.appointment.dto.request;

import com.hospital.backend.enums.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para la actualización de una cita médica existente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {

    @FutureOrPresent(message = "La fecha debe ser el día de hoy o posterior")
    private LocalDate appointmentDate;

    private LocalTime startTime;

    @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
    private String reason;

    private String notes;

    @NotNull(message = "El estado de la cita es obligatorio")
    private AppointmentStatus status;

    private Boolean inPerson;

    private String virtualMeetingUrl;
    
    private String cancellationReason;
} 