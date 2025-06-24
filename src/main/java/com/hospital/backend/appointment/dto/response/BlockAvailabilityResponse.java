package com.hospital.backend.appointment.dto.response;

import com.hospital.backend.enums.TimeBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar la disponibilidad de un bloque de tiempo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockAvailabilityResponse {
    
    private TimeBlock timeBlock;
    private Boolean isAvailable;
    private Integer maxPatients;
    private Integer currentPatients;
    private Integer remainingSlots;
    private Long doctorId;
} 