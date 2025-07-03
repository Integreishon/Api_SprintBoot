package com.hospital.backend.chatbot.repository;

import com.hospital.backend.chatbot.entity.ChatbotKnowledgeBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la base de conocimientos del chatbot
 */
@Repository
public interface ChatbotKnowledgeBaseRepository extends JpaRepository<ChatbotKnowledgeBase, Long> {

    /**
     * Buscar entradas activas por palabras clave
     */
    @Query("SELECT kb FROM ChatbotKnowledgeBase kb WHERE kb.isActive = true AND " +
           "(LOWER(kb.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(kb.answer) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(kb.keywords) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ChatbotKnowledgeBase> findActiveByKeyword(@Param("keyword") String keyword);
    
    /**
     * Buscar entradas activas por múltiples palabras clave con prioridad
     */
    @Query("SELECT kb FROM ChatbotKnowledgeBase kb WHERE kb.isActive = true AND " +
           "(" +
           "   LOWER(kb.question) LIKE LOWER(CONCAT('%', :keyword1, '%')) OR " +
           "   LOWER(kb.answer) LIKE LOWER(CONCAT('%', :keyword1, '%')) OR " +
           "   LOWER(kb.keywords) LIKE LOWER(CONCAT('%', :keyword1, '%')) OR " +
           "   LOWER(kb.question) LIKE LOWER(CONCAT('%', :keyword2, '%')) OR " +
           "   LOWER(kb.answer) LIKE LOWER(CONCAT('%', :keyword2, '%')) OR " +
           "   LOWER(kb.keywords) LIKE LOWER(CONCAT('%', :keyword2, '%')) OR " +
           "   LOWER(kb.question) LIKE LOWER(CONCAT('%', :keyword3, '%')) OR " +
           "   LOWER(kb.answer) LIKE LOWER(CONCAT('%', :keyword3, '%')) OR " +
           "   LOWER(kb.keywords) LIKE LOWER(CONCAT('%', :keyword3, '%'))" +
           ") " +
           "ORDER BY CASE " +
           "    WHEN LOWER(kb.question) LIKE LOWER(CONCAT('%', :keyword1, '%', :keyword2, '%', :keyword3, '%')) THEN 1 " + // Coincidencia de frase
           "    WHEN LOWER(kb.question) LIKE LOWER(CONCAT('%', :keyword1, '%')) AND LOWER(kb.question) LIKE LOWER(CONCAT('%', :keyword2, '%')) AND LOWER(kb.question) LIKE LOWER(CONCAT('%', :keyword3, '%')) THEN 2 " + // Todas las palabras clave en la pregunta
           "    WHEN LOWER(kb.question) LIKE LOWER(CONCAT('%', :keyword1, '%')) THEN 3 " + // Palabra clave principal en la pregunta
           "    WHEN LOWER(kb.keywords) LIKE LOWER(CONCAT('%', :keyword1, '%')) THEN 4 " + // Palabra clave principal en las keywords
           "    ELSE 5 " +
           "END, kb.priority DESC, kb.usageCount DESC")
    List<ChatbotKnowledgeBase> findActiveByMultipleKeywords(
            @Param("keyword1") String keyword1, 
            @Param("keyword2") String keyword2, 
            @Param("keyword3") String keyword3);
    
    /**
     * Buscar entradas por tema
     */
    List<ChatbotKnowledgeBase> findByTopicAndIsActiveTrue(String topic);
    
    /**
     * Buscar entradas por categoría
     */
    List<ChatbotKnowledgeBase> findByCategoryAndIsActiveTrue(String category);
    
    /**
     * Buscar entradas por categoría y subcategoría
     */
    List<ChatbotKnowledgeBase> findByCategoryAndSubcategoryAndIsActiveTrue(String category, String subcategory);
    
    /**
     * Buscar entradas por entidad relacionada
     */
    List<ChatbotKnowledgeBase> findByRelatedEntityTypeAndRelatedEntityIdAndIsActiveTrue(
            String relatedEntityType, Long relatedEntityId);
    
    /**
     * Incrementar contador de uso
     */
    @Modifying
    @Query("UPDATE ChatbotKnowledgeBase kb SET kb.usageCount = kb.usageCount + 1 WHERE kb.id = :id")
    void incrementUsageCount(@Param("id") Long id);
    
    /**
     * Actualizar tasa de éxito
     */
    @Modifying
    @Query("UPDATE ChatbotKnowledgeBase kb SET kb.successRate = :rate WHERE kb.id = :id")
    void updateSuccessRate(@Param("id") Long id, @Param("rate") Double rate);
    
    /**
     * Buscar entradas paginadas con filtros
     */
    Page<ChatbotKnowledgeBase> findByIsActiveAndTopicContainingIgnoreCaseAndCategoryContainingIgnoreCase(
            Boolean isActive, String topic, String category, Pageable pageable);
    
    /**
     * Buscar preguntas frecuentes (entradas más utilizadas)
     */
    @Query("SELECT kb FROM ChatbotKnowledgeBase kb WHERE kb.isActive = true ORDER BY kb.usageCount DESC")
    List<ChatbotKnowledgeBase> findTopFAQs(Pageable pageable);
    
    /**
     * Buscar entradas con mejor tasa de éxito
     */
    @Query("SELECT kb FROM ChatbotKnowledgeBase kb WHERE kb.isActive = true AND kb.usageCount > 5 ORDER BY kb.successRate DESC")
    List<ChatbotKnowledgeBase> findMostSuccessful(Pageable pageable);
    
    /**
     * Contar entradas activas por categoría
     */
    @Query("SELECT kb.category, COUNT(kb) FROM ChatbotKnowledgeBase kb WHERE kb.isActive = true GROUP BY kb.category")
    List<Object[]> countActiveEntriesByCategory();
} 