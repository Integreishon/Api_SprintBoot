package com.hospital.backend.chatbot.service;

import com.hospital.backend.chatbot.dto.request.KnowledgeBaseEntryRequest;
import com.hospital.backend.chatbot.dto.response.KnowledgeBaseEntryResponse;
import com.hospital.backend.chatbot.entity.ChatbotKnowledgeBase;
import com.hospital.backend.chatbot.repository.ChatbotKnowledgeBaseRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de la base de conocimientos del chatbot
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotKnowledgeService {

    private final ChatbotKnowledgeBaseRepository knowledgeBaseRepository;
    
    /**
     * Crear una nueva entrada en la base de conocimientos
     */
    @Transactional
    public KnowledgeBaseEntryResponse createKnowledgeBaseEntry(KnowledgeBaseEntryRequest request) {
        ChatbotKnowledgeBase entry = new ChatbotKnowledgeBase();
        mapRequestToEntity(request, entry);
        
        ChatbotKnowledgeBase savedEntry = knowledgeBaseRepository.save(entry);
        
        return mapToResponse(savedEntry);
    }
    
    /**
     * Obtener una entrada por su ID
     */
    public KnowledgeBaseEntryResponse getKnowledgeBaseEntry(Long id) {
        ChatbotKnowledgeBase entry = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrada no encontrada con ID: " + id));
        
        return mapToResponse(entry);
    }
    
    /**
     * Actualizar una entrada existente
     */
    @Transactional
    public KnowledgeBaseEntryResponse updateKnowledgeBaseEntry(Long id, KnowledgeBaseEntryRequest request) {
        ChatbotKnowledgeBase entry = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrada no encontrada con ID: " + id));
        
        mapRequestToEntity(request, entry);
        
        ChatbotKnowledgeBase updatedEntry = knowledgeBaseRepository.save(entry);
        
        return mapToResponse(updatedEntry);
    }
    
    /**
     * Eliminar una entrada
     */
    @Transactional
    public void deleteKnowledgeBaseEntry(Long id) {
        if (!knowledgeBaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entrada no encontrada con ID: " + id);
        }
        
        knowledgeBaseRepository.deleteById(id);
    }
    
    /**
     * Activar/desactivar una entrada
     */
    @Transactional
    public KnowledgeBaseEntryResponse toggleActive(Long id) {
        ChatbotKnowledgeBase entry = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrada no encontrada con ID: " + id));
        
        entry.setIsActive(!entry.getIsActive());
        
        ChatbotKnowledgeBase updatedEntry = knowledgeBaseRepository.save(entry);
        
        return mapToResponse(updatedEntry);
    }
    
    /**
     * Obtener entradas paginadas con filtros
     */
    public PageResponse<KnowledgeBaseEntryResponse> getKnowledgeBaseEntries(
            Boolean isActive, String topic, String category, Pageable pageable) {
        
        // Valores por defecto
        isActive = isActive != null ? isActive : true;
        topic = topic != null ? topic : "";
        category = category != null ? category : "";
        
        Page<ChatbotKnowledgeBase> entries = knowledgeBaseRepository
                .findByIsActiveAndTopicContainingIgnoreCaseAndCategoryContainingIgnoreCase(
                        isActive, topic, category, pageable);
        
        Page<KnowledgeBaseEntryResponse> entryResponses = entries.map(this::mapToResponse);
        return new PageResponse<>(entryResponses);
    }
    
    /**
     * Obtener entradas por tema
     */
    public List<KnowledgeBaseEntryResponse> getEntriesByTopic(String topic) {
        List<ChatbotKnowledgeBase> entries = knowledgeBaseRepository.findByTopicAndIsActiveTrue(topic);
        
        return entries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener entradas por categoría
     */
    public List<KnowledgeBaseEntryResponse> getEntriesByCategory(String category) {
        List<ChatbotKnowledgeBase> entries = knowledgeBaseRepository.findByCategoryAndIsActiveTrue(category);
        
        return entries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener preguntas frecuentes
     */
    public List<KnowledgeBaseEntryResponse> getTopFAQs(int limit) {
        List<ChatbotKnowledgeBase> entries = knowledgeBaseRepository.findTopFAQs(Pageable.ofSize(limit));
        
        return entries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Incrementar contador de uso
     */
    @Transactional
    public void incrementUsageCount(Long id) {
        knowledgeBaseRepository.incrementUsageCount(id);
    }
    
    /**
     * Actualizar tasa de éxito
     */
    @Transactional
    public void updateSuccessRate(Long id, Double rate) {
        knowledgeBaseRepository.updateSuccessRate(id, rate);
    }
    
    /**
     * Contar entradas por categoría
     */
    public Map<String, Long> countEntriesByCategory() {
        List<Object[]> results = knowledgeBaseRepository.countActiveEntriesByCategory();
        
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }
    
    /**
     * Buscar entradas por palabra clave
     */
    public List<KnowledgeBaseEntryResponse> searchByKeyword(String keyword) {
        List<ChatbotKnowledgeBase> entries = knowledgeBaseRepository.findActiveByKeyword(keyword);
        
        return entries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar entradas por múltiples palabras clave
     */
    public List<KnowledgeBaseEntryResponse> searchByMultipleKeywords(
            String keyword1, String keyword2, String keyword3) {
        
        List<ChatbotKnowledgeBase> entries = knowledgeBaseRepository
                .findActiveByMultipleKeywords(keyword1, keyword2, keyword3);
        
        return entries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Mapear solicitud a entidad
     */
    private void mapRequestToEntity(KnowledgeBaseEntryRequest request, ChatbotKnowledgeBase entity) {
        entity.setTopic(request.getTopic());
        entity.setQuestion(request.getQuestion());
        entity.setAnswer(request.getAnswer());
        entity.setKeywords(request.getKeywords());
        entity.setDataType(request.getDataType());
        entity.setIsActive(request.getIsActive());
        entity.setPriority(request.getPriority());
        entity.setCategory(request.getCategory());
        entity.setSubcategory(request.getSubcategory());
        entity.setExternalReference(request.getExternalReference());
        entity.setRelatedEntityType(request.getRelatedEntityType());
        entity.setRelatedEntityId(request.getRelatedEntityId());
        entity.setMetadata(request.getMetadata());
    }
    
    /**
     * Mapear entidad a respuesta
     */
    private KnowledgeBaseEntryResponse mapToResponse(ChatbotKnowledgeBase entity) {
        KnowledgeBaseEntryResponse response = new KnowledgeBaseEntryResponse();
        
        response.setId(entity.getId());
        response.setTopic(entity.getTopic());
        response.setQuestion(entity.getQuestion());
        response.setAnswer(entity.getAnswer());
        response.setKeywords(entity.getKeywords());
        response.setDataType(entity.getDataType());
        response.setDataTypeName(entity.getDataType().getDisplayName());
        response.setIsActive(entity.getIsActive());
        response.setPriority(entity.getPriority());
        response.setCategory(entity.getCategory());
        response.setSubcategory(entity.getSubcategory());
        response.setExternalReference(entity.getExternalReference());
        response.setUsageCount(entity.getUsageCount());
        response.setSuccessRate(entity.getSuccessRate());
        response.setRelatedEntityType(entity.getRelatedEntityType());
        response.setRelatedEntityId(entity.getRelatedEntityId());
        response.setMetadata(entity.getMetadata());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setUpdatedBy(entity.getUpdatedBy());
        
        return response;
    }
} 