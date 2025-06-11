package com.hospital.backend.chatbot.entity;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.common.entity.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa una conversación con el chatbot
 */
@Entity
@Table(name = "chatbot_conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotConversation extends AuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @Column(name = "query", nullable = false, columnDefinition = "TEXT")
    private String query;
    
    @Column(name = "response", nullable = false, columnDefinition = "TEXT")
    private String response;
    
    @Column(name = "context", columnDefinition = "TEXT")
    private String context; // JSON con el contexto de la conversación
    
    @Column(name = "is_successful", nullable = false)
    private Boolean isSuccessful = true;
    
    @Column(name = "feedback_rating")
    private Integer feedbackRating; // 1-5
    
    @Column(name = "feedback_text", columnDefinition = "TEXT")
    private String feedbackText;
    
    @Column(name = "response_time_ms")
    private Long responseTimeMs;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "source")
    private String source; // web, mobile, etc.
    
    @Column(name = "is_handled_by_human")
    private Boolean isHandledByHuman = false;
    
    @Column(name = "human_agent_id")
    private Long humanAgentId;
    
    @Column(name = "transferred_at")
    private LocalDateTime transferredAt;
    
    @Column(name = "knowledge_base_entries_used", columnDefinition = "TEXT")
    private String knowledgeBaseEntriesUsed; // IDs de entradas utilizadas, en formato JSON
    
    @Column(name = "actions_taken", columnDefinition = "TEXT")
    private String actionsTaken; // Acciones realizadas por el chatbot, en formato JSON
    
    @Column(name = "intent_detected")
    private String intentDetected; // Intención detectada
    
    @Column(name = "entities_detected", columnDefinition = "TEXT")
    private String entitiesDetected; // Entidades detectadas, en formato JSON
} 