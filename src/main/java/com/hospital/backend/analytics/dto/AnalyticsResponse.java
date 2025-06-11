package com.hospital.backend.analytics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.backend.enums.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO para respuestas de análisis con métricas y gráficos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private PeriodType periodType;
    
    private Map<String, Object> metrics;
    
    private Map<String, Object> previousPeriodMetrics;
    
    private List<ChartData> charts;
    
    private Boolean fromCache;
    
    private Long generationTimeMs;
    
    private Map<String, Object> metadata;
    
    // Constructor básico
    public AnalyticsResponse(LocalDate startDate, LocalDate endDate, PeriodType periodType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodType = periodType;
        this.metrics = new HashMap<>();
        this.charts = new ArrayList<>();
        this.fromCache = false;
    }
    
    /**
     * Clase para representar datos de un gráfico
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChartData {
        private String chartId;
        private String title;
        private String type; // pie, bar, line, etc.
        private List<String> labels;
        private List<Series> series;
        private Map<String, Object> options;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Series {
            private String name;
            private List<Object> data;
            private String color;
        }
    }
    
    /**
     * Agrega una métrica a la respuesta
     */
    public void addMetric(String key, Object value) {
        if (this.metrics == null) {
            this.metrics = new HashMap<>();
        }
        this.metrics.put(key, value);
    }
    
    /**
     * Agrega un gráfico a la respuesta
     */
    public void addChart(ChartData chart) {
        if (this.charts == null) {
            this.charts = new ArrayList<>();
        }
        this.charts.add(chart);
    }
} 