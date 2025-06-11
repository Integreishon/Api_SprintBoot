package com.hospital.backend.chatbot.dto.response;

import com.hospital.backend.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de entrada de la base de conocimientos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseEntryResponse {

    private Long id;
    
    private String topic;
    
    private String question;
    
    private String answer;
    
    private String keywords;
    
    private DataType dataType;
    
    private String dataTypeName;
    
    private Boolean isActive;
    
    private Integer priority;
    
    private String category;
    
    private String subcategory;
    
    private String externalReference;
    
    private Integer usageCount;
    
    private Double successRate;
    
    private String relatedEntityType;
    
    private Long relatedEntityId;
    
    private String metadata;
    
    private LocalDateTime createdAt;
    
    private String createdBy;
    
    private LocalDateTime updatedAt;
    
    private String updatedBy;
} 