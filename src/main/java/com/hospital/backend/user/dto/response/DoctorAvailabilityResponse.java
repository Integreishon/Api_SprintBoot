// DTO de respuesta para disponibilidad de doctor
package com.hospital.backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityResponse {
    private Long doctorId;
    private String doctorName;
    private int dayOfWeek;
    private String dayName;
    private List<TimeSlotDto> timeSlots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotDto {
        private Long id;
        private LocalTime startTime;
        private LocalTime endTime;
        private int slotDuration; // en minutos
        private String startTimeFormatted;
        private String endTimeFormatted;
    }
}