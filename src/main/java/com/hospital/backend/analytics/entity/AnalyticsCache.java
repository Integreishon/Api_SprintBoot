package com.hospital.backend.analytics.entity;

import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.PeriodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidad para almacenar en caché métricas calculadas para mejorar el rendimiento
 */
@Entity
@Table(name = "analytics_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsCache extends BaseEntity {

    @Column(name = "metric_key", nullable = false, length = 100)
    private String metricKey;
    
    @Column(name = "metric_value", columnDefinition = "TEXT", nullable = false)
    private String metricValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    private PeriodType periodType;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "filter_criteria", columnDefinition = "TEXT")
    private String filterCriteria;
    
    @Column(name = "data_source", length = 100)
    private String dataSource;
    
    @Column(name = "ttl_seconds")
    private Integer ttlSeconds;
    
    @Column(name = "generation_time_ms")
    private Long generationTimeMs;
    
    @Column(name = "is_expired", nullable = false)
    private Boolean isExpired = false;
    
    @Column(name = "series_labels", columnDefinition = "TEXT")
    private String seriesLabels;
    
    @Column(name = "series_data", columnDefinition = "TEXT")
    private String seriesData;
    
    @Column(name = "chart_type", length = 50)
    private String chartType;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    /**
     * Verifica si el caché ha expirado basado en su TTL
     */
    public boolean hasExpired() {
        if (this.isExpired) {
            return true;
        }
        
        if (this.ttlSeconds == null || this.ttlSeconds <= 0) {
            return false;
        }
        
        if (this.getCreatedAt() == null) {
            return true;
        }
        
        // Calcular si ha pasado más tiempo que el TTL desde la creación
        return this.getCreatedAt().plusSeconds(this.ttlSeconds).isBefore(java.time.LocalDateTime.now());
    }
    
    /**
     * Marca el caché como expirado
     */
    public void expire() {
        this.isExpired = true;
    }
} 