package com.hospital.backend.analytics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.backend.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de logs de auditoría
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;
    
    private Long userId;
    
    private String username;
    
    private String userIp;
    
    private OperationType operationType;
    
    private String operationTypeName;
    
    private String entityName;
    
    private Long entityId;
    
    private String description;
    
    private String requestUrl;
    
    private String requestMethod;
    
    private Boolean successful;
    
    private String errorMessage;
    
    private Long executionTimeMs;
    
    private String module;
    
    private String action;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String formattedDate;
    
    // Campos adicionales para UI
    private String statusLabel;
    private String statusClass;
    
    /**
     * Establece etiquetas de estado para la UI
     */
    public void setUILabels() {
        if (successful != null && successful) {
            this.statusLabel = "Éxito";
            this.statusClass = "success";
        } else {
            this.statusLabel = "Error";
            this.statusClass = "danger";
        }
    }
} 