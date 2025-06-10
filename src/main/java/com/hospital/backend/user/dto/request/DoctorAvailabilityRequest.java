// DTO para configurar disponibilidad horaria de doctores
package com.hospital.backend.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalTime;

@Data
public class DoctorAvailabilityRequest {
    
    @NotNull(message = "El día de la semana es obligatorio")
    @Min(value = 1, message = "El día de la semana debe ser entre 1 (Lunes) y 6 (Sábado)")
    @Max(value = 6, message = "El día de la semana debe ser entre 1 (Lunes) y 6 (Sábado)")
    private Integer dayOfWeek;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;
    
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;
    
    @NotNull(message = "La duración del slot es obligatoria")
    @Min(value = 15, message = "La duración mínima del slot es 15 minutos")
    @Max(value = 120, message = "La duración máxima del slot es 120 minutos")
    private Integer slotDuration;
    
    private Boolean isActive = true;
}