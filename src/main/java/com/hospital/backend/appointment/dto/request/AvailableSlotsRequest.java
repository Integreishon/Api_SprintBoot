package com.hospital.backend.appointment.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para la solicitud de slots de tiempo disponibles para citas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotsRequest {

    @NotNull(message = "El ID del doctor es obligatorio")
    private Long doctorId;

    @NotNull(message = "El ID de la especialidad es obligatorio")
    private Long specialtyId;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser el día de hoy o posterior")
    private LocalDate date;

    private Integer slotDuration; // en minutos, si no se proporciona se usa el predeterminado de la especialidad

    // Rango de horas para filtrar (opcional)
    private String startHour; // Formato: "HH:mm"
    private String endHour; // Formato: "HH:mm"

    private Boolean inPerson = true;
} 