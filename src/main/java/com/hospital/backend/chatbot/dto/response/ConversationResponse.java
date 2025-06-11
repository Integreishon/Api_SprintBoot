package com.hospital.backend.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para la respuesta de la conversación con el chatbot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String sessionId;
    
    private String query;
    
    private String response;
    
    private String context;
    
    private Boolean isSuccessful;
    
    private Integer feedbackRating;
    
    private String feedbackText;
    
    private Long responseTimeMs;
    
    private String ipAddress;
    
    private String userAgent;
    
    private String source;
    
    private Boolean isHandledByHuman;
    
    private Long humanAgentId;
    
    private String humanAgentName;
    
    private LocalDateTime transferredAt;
    
    private String knowledgeBaseEntriesUsed;
    
    private String actionsTaken;
    
    private String intentDetected;
    
    private String entitiesDetected;
    
    private LocalDateTime createdAt;
    
    // Convertido de JSON a Map para uso en la aplicación
    private Map<String, Object> contextAsMap;
    
    private Map<String, Object> knowledgeBaseEntriesAsMap;
    
    private Map<String, Object> actionsAsMap;
    
    private Map<String, Object> entitiesAsMap;
} 