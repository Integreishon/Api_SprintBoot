// DTO de respuesta para disponibilidad de doctor
package com.hospital.backend.user.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DoctorAvailabilityResponse {
    
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Integer dayOfWeek;
    private String dayName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDuration;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}