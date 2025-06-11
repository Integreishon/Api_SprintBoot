package com.hospital.backend.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DTO para la respuesta del chatbot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponse {

    private Long conversationId;
    
    private String sessionId;
    
    private String query;
    
    private String response;
    
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private Long responseTimeMs;
    
    private Boolean isSuccessful = true;
    
    private String intentDetected;
    
    private Map<String, Object> entitiesDetected;
    
    private List<String> suggestedQuestions = new ArrayList<>();
    
    private List<KnowledgeBaseReferenceResponse> references = new ArrayList<>();
    
    private Map<String, Object> actions;
    
    private Boolean needsHumanIntervention = false;
    
    private String errorMessage;
    
    private String confidence;
    
    private Boolean isLearning = false;
    
    // Método para agregar una referencia
    public void addReference(KnowledgeBaseReferenceResponse reference) {
        if (this.references == null) {
            this.references = new ArrayList<>();
        }
        this.references.add(reference);
    }
    
    // Método para agregar una pregunta sugerida
    public void addSuggestedQuestion(String question) {
        if (this.suggestedQuestions == null) {
            this.suggestedQuestions = new ArrayList<>();
        }
        this.suggestedQuestions.add(question);
    }
} 