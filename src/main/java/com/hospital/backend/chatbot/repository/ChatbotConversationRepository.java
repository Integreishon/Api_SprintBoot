package com.hospital.backend.chatbot.repository;

import com.hospital.backend.chatbot.entity.ChatbotConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para las conversaciones del chatbot
 */
@Repository
public interface ChatbotConversationRepository extends JpaRepository<ChatbotConversation, Long> {

    /**
     * Buscar conversaciones por usuario
     */
    Page<ChatbotConversation> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Buscar conversaciones por sesión
     */
    List<ChatbotConversation> findBySessionIdOrderByCreatedAtAsc(String sessionId);
    
    /**
     * Buscar últimas conversaciones por sesión
     */
    @Query("SELECT c FROM ChatbotConversation c WHERE c.sessionId = :sessionId ORDER BY c.createdAt DESC")
    List<ChatbotConversation> findLatestBySessionId(@Param("sessionId") String sessionId, Pageable pageable);
    
    /**
     * Buscar conversaciones por usuario y fecha
     */
    @Query("SELECT c FROM ChatbotConversation c WHERE c.user.id = :userId AND c.createdAt BETWEEN :startDate AND :endDate")
    Page<ChatbotConversation> findByUserIdAndDateRange(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Buscar conversaciones con feedback
     */
    Page<ChatbotConversation> findByFeedbackRatingIsNotNull(Pageable pageable);
    
    /**
     * Buscar conversaciones con feedback negativo
     */
    @Query("SELECT c FROM ChatbotConversation c WHERE c.feedbackRating <= 2 OR c.isSuccessful = false")
    Page<ChatbotConversation> findWithNegativeFeedback(Pageable pageable);
    
    /**
     * Buscar conversaciones sin respuesta exitosa
     */
    Page<ChatbotConversation> findByIsSuccessfulFalse(Pageable pageable);
    
    /**
     * Buscar conversaciones transferidas a agentes humanos
     */
    Page<ChatbotConversation> findByIsHandledByHumanTrue(Pageable pageable);
    
    /**
     * Buscar por texto de consulta (búsqueda de texto completo)
     */
    @Query("SELECT c FROM ChatbotConversation c WHERE " +
           "LOWER(c.query) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
           "LOWER(c.response) LIKE LOWER(CONCAT('%', :text, '%'))")
    Page<ChatbotConversation> searchByText(@Param("text") String text, Pageable pageable);
    
    /**
     * Contar conversaciones por día en el último mes
     */
    @Query("SELECT CAST(c.createdAt AS DATE), COUNT(c) FROM ChatbotConversation c " +
           "WHERE c.createdAt >= :startDate GROUP BY CAST(c.createdAt AS DATE)")
    List<Object[]> countByDayLastMonth(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Calcular tiempo promedio de respuesta
     */
    @Query("SELECT AVG(c.responseTimeMs) FROM ChatbotConversation c WHERE c.createdAt >= :startDate")
    Long averageResponseTime(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Distribución de ratings de feedback
     */
    @Query("SELECT c.feedbackRating, COUNT(c) FROM ChatbotConversation c " +
           "WHERE c.feedbackRating IS NOT NULL GROUP BY c.feedbackRating")
    List<Object[]> countByFeedbackRating();
    
    /**
     * Tasa de éxito general
     */
    @Query("SELECT COUNT(c) FROM ChatbotConversation c WHERE c.isSuccessful = true AND c.createdAt >= :startDate")
    Long countSuccessful(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(c) FROM ChatbotConversation c WHERE c.createdAt >= :startDate")
    Long countTotal(@Param("startDate") LocalDateTime startDate);
} 