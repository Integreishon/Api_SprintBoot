// DTO de respuesta para disponibilidad de doctor
package com.hospital.backend.user.dto.response;

import com.hospital.backend.enums.TimeBlock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para disponibilidad de doctor
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityResponse {
    private Long doctorId;
    private String doctorName;
    private int dayOfWeek;
    private String dayName;
    private List<BlockDto> blocks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlockDto {
        private Long id;
        private TimeBlock timeBlock;
        private String blockName;
        private Integer maxPatients;
        private Boolean isAvailable;
    }
}