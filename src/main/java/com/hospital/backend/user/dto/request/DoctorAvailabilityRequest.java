// DTO para configurar disponibilidad horaria de doctores
package com.hospital.backend.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityRequest {
    
    @NotNull(message = "El día de la semana es obligatorio")
    @Min(value = 1, message = "El día debe ser mínimo 1 (Lunes)")
    @Max(value = 7, message = "El día debe ser máximo 7 (Domingo)")
    private Integer dayOfWeek;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;
    
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;
    
    @Min(value = 10, message = "La duración mínima del turno es 10 minutos")
    private Integer slotDuration = 30; // 30 minutos por defecto
}