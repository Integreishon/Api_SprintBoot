package com.hospital.backend.admin.dto;

import java.time.LocalDateTime;

import com.hospital.backend.enums.DataType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de configuraciones del hospital
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingResponse {

    private Long id;
    
    private String key;
    
    private String value;
    
    private DataType dataType;
    
    private String description;
    
    private String category;
    
    private Boolean isPublic;
    
    private Boolean isEditable;
    
    private String createdBy;
    
    private String updatedBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * Verifica si la configuración es una configuración del sistema
     * @return true si es una configuración del sistema
     */
    public boolean isSystemSetting() {
        return !isEditable;
    }
} 