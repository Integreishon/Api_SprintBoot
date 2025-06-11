package com.hospital.backend.appointment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para la respuesta de slots de tiempo disponibles para citas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotResponse {

    private Long doctorId;
    private String doctorName;
    private Long specialtyId;
    private String specialtyName;
    private LocalDate date;
    private String dayOfWeek;
    private Integer slotDuration; // en minutos
    private List<TimeSlot> availableSlots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlot {
        private String startTime;
        private String endTime;
        private Boolean available;
    }
} 