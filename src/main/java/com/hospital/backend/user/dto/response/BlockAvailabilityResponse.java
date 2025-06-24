package com.hospital.backend.user.dto.response;

import com.hospital.backend.enums.TimeBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta para disponibilidad por bloques de tiempo
 * Usado en el sistema de Urovital para representar disponibilidad en bloques de mañana/tarde
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockAvailabilityResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private LocalDate date;
    private TimeBlock timeBlock;
    private boolean isAvailable;
} 