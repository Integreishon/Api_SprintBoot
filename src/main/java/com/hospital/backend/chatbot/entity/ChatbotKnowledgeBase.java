package com.hospital.backend.chatbot.entity;

import com.hospital.backend.common.entity.AuditEntity;
import com.hospital.backend.enums.DataType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un elemento de la base de conocimientos del chatbot
 */
@Entity
@Table(name = "chatbot_knowledge_base")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotKnowledgeBase extends AuditEntity {

    @Column(name = "topic", nullable = false)
    private String topic;
    
    @Column(name = "question", nullable = false)
    private String question;
    
    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;
    
    @Column(name = "keywords", nullable = false)
    private String keywords;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    private DataType dataType = DataType.STRING;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "priority", nullable = false)
    private Integer priority = 5; // 1-10, siendo 10 la prioridad más alta
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "subcategory")
    private String subcategory;
    
    @Column(name = "external_reference")
    private String externalReference;
    
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;
    
    @Column(name = "success_rate", nullable = false)
    private Double successRate = 0.0;
    
    @Column(name = "related_entity_type")
    private String relatedEntityType;
    
    @Column(name = "related_entity_id")
    private Long relatedEntityId;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON con metadatos adicionales
} 