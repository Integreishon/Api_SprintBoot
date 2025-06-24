package com.hospital.backend.appointment.dto.request;

import com.hospital.backend.enums.TimeBlock;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para actualizar una cita médica existente
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {
    
    @FutureOrPresent(message = "La fecha de la cita debe ser hoy o en el futuro")
    private LocalDate appointmentDate;
    
    private TimeBlock timeBlock;
    
    @NotBlank(message = "El motivo de la consulta no puede estar vacío")
    private String reason;
    
    private Long referralId;
}
