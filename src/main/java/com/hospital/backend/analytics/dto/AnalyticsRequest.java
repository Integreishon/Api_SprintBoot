package com.hospital.backend.analytics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.backend.enums.PeriodType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO para solicitudes de análisis con filtros
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRequest {

    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @NotNull(message = "El tipo de período es obligatorio")
    private PeriodType periodType;
    
    private List<String> metrics;
    
    private List<Long> doctorIds;
    
    private List<Long> specialtyIds;
    
    private List<Long> patientIds;
    
    private String groupBy;
    
    private Boolean includeCharts = true;
    
    private Boolean useCache = true;
    
    private Boolean compareWithPreviousPeriod = false;
    
    private Map<String, Object> additionalFilters;
    
    private List<String> chartTypes;
} 