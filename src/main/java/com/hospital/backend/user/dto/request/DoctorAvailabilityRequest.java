// DTO para configurar disponibilidad horaria de doctores
package com.hospital.backend.user.dto.request;

import com.hospital.backend.enums.TimeBlock;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para configurar disponibilidad horaria de doctores
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityRequest {
    
    @NotNull(message = "El día de la semana es obligatorio")
    @Min(value = 1, message = "El día debe ser mínimo 1 (Lunes)")
    @Max(value = 7, message = "El día debe ser máximo 7 (Domingo)")
    private Integer dayOfWeek;
    
    @NotNull(message = "El bloque de tiempo es obligatorio")
    private TimeBlock timeBlock;
    
    @Min(value = 1, message = "La capacidad mínima es 1 paciente")
    private Integer maxPatients;
    
    private Boolean isAvailable = true;
}