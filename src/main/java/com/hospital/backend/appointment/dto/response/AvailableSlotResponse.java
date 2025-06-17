package com.hospital.backend.appointment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO de respuesta para slots de tiempo disponibles
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotResponse {
    
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer duration; // en minutos
    private boolean available;
    private String displayTime; // Formato para mostrar (ej: "09:00 - 09:30")
    
    // Constructor sin displayTime (se calcula automáticamente)
    public AvailableSlotResponse(LocalTime startTime, LocalTime endTime, Integer duration, boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.available = available;
        this.displayTime = generateDisplayTime();
    }
    
    /**
     * Generar formato de display automáticamente
     */
    private String generateDisplayTime() {
        if (startTime != null && endTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return startTime.format(formatter) + " - " + endTime.format(formatter);
        }
        return "";
    }
    
    /**
     * Actualizar displayTime cuando se cambian los tiempos
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        this.displayTime = generateDisplayTime();
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        this.displayTime = generateDisplayTime();
    }
}
