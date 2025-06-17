package com.hospital.backend.appointment.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO para solicitar slots disponibles de un doctor
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotsRequest {
    
    @NotNull(message = "El ID del doctor es obligatorio")
    private Long doctorId;
    
    @NotNull(message = "El ID de la especialidad es obligatorio")
    private Long specialtyId;
    
    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro")
    private LocalDate date;
}
