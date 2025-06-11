package com.hospital.backend.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para las referencias a entradas de la base de conocimientos en respuestas del chatbot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseReferenceResponse {

    private Long id;
    
    private String topic;
    
    private String question;
    
    private String answer;
    
    private String category;
    
    private String subcategory;
    
    private String externalReference;
    
    private Double relevanceScore; // Puntuación de relevancia (0-1)
} 