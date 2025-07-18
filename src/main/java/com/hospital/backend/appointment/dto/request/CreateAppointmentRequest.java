package com.hospital.backend.appointment.dto.request;

import com.hospital.backend.enums.TimeBlock;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO para crear una nueva cita médica
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateAppointmentRequest {
    
    @NotNull(message = "El ID del doctor es obligatorio")
    private Long doctorId;
    
    @NotNull(message = "El ID de la especialidad es obligatorio")
    private Long specialtyId;
    
    @NotNull(message = "La fecha de la cita es obligatoria")
    @FutureOrPresent(message = "La fecha de la cita no puede ser en el pasado")
    private LocalDate appointmentDate;
    
    @NotNull(message = "El bloque de tiempo es obligatorio")
    private TimeBlock timeBlock;
    
    @NotBlank(message = "El motivo de la consulta es obligatorio")
    private String reason;
    
    private Long referralId;
}
