package com.hospital.backend.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.repository.UserRepository;
import com.hospital.backend.chatbot.dto.request.ChatbotQueryRequest;
import com.hospital.backend.chatbot.dto.request.FeedbackRequest;
import com.hospital.backend.chatbot.dto.response.ChatbotResponse;
import com.hospital.backend.chatbot.dto.response.ConversationResponse;
import com.hospital.backend.chatbot.dto.response.KnowledgeBaseEntryResponse;
import com.hospital.backend.chatbot.dto.response.KnowledgeBaseReferenceResponse;
import com.hospital.backend.chatbot.entity.ChatbotConversation;

import com.hospital.backend.chatbot.repository.ChatbotConversationRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio principal del chatbot
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final ChatbotConversationRepository conversationRepository;
    private final ChatbotKnowledgeService knowledgeService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    // Número máximo de palabras clave a extraer de la consulta
    private static final int MAX_KEYWORDS = 5;
    
    /**
     * Procesar una consulta al chatbot
     */
    @Transactional
    public ChatbotResponse processQuery(ChatbotQueryRequest request) {
        long startTime = System.currentTimeMillis();
        
        // Crear respuesta
        ChatbotResponse response = new ChatbotResponse();
        response.setSessionId(request.getSessionId());
        response.setQuery(request.getQuery());
        
        try {
            // Preparar el texto de la consulta
            String query = prepareText(request.getQuery());
            
            // Extraer palabras clave de la consulta
            List<String> keywords = extractKeywords(query);
            
            // Buscar entradas relevantes en la base de conocimientos
            List<KnowledgeBaseEntryResponse> relevantEntries = findRelevantEntries(keywords);
            
            if (relevantEntries.isEmpty()) {
                // No se encontraron coincidencias
                response.setResponse("Lo siento, no tengo información sobre eso. "
                        + "¿Podría reformular su pregunta o ser más específico?");
                response.setIsSuccessful(false);
                response.setNeedsHumanIntervention(true);
            } else {
                // Generar respuesta basada en las entradas más relevantes
                KnowledgeBaseEntryResponse bestMatch = relevantEntries.get(0);
                
                // Actualizar contador de uso para la entrada más relevante
                knowledgeService.incrementUsageCount(bestMatch.getId());
                
                // Configurar respuesta
                response.setResponse(bestMatch.getAnswer());
                response.setIsSuccessful(true);
                
                // Agregar referencias a las entradas utilizadas
                for (KnowledgeBaseEntryResponse entry : relevantEntries.subList(0, Math.min(3, relevantEntries.size()))) {
                    KnowledgeBaseReferenceResponse ref = new KnowledgeBaseReferenceResponse();
                    ref.setId(entry.getId());
                    ref.setTopic(entry.getTopic());
                    ref.setQuestion(entry.getQuestion());
                    ref.setAnswer(entry.getAnswer());
                    ref.setCategory(entry.getCategory());
                    ref.setSubcategory(entry.getSubcategory());
                    ref.setExternalReference(entry.getExternalReference());
                    
                    // Calcular puntuación de relevancia (simplificada para el ejemplo)
                    ref.setRelevanceScore(entry.equals(bestMatch) ? 1.0 : 0.7);
                    
                    response.addReference(ref);
                }
                
                // Sugerir preguntas relacionadas
                for (KnowledgeBaseEntryResponse entry : relevantEntries.subList(0, Math.min(5, relevantEntries.size()))) {
                    if (!entry.equals(bestMatch)) {
                        response.addSuggestedQuestion(entry.getQuestion());
                    }
                }
            }
            
            // Guardar la conversación
            saveConversation(request, response, System.currentTimeMillis() - startTime);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error al procesar consulta del chatbot: {}", e.getMessage());
            
            // Configurar respuesta de error
            response.setResponse("Lo siento, ocurrió un error al procesar su consulta. "
                    + "Por favor, inténtelo de nuevo más tarde o contacte con soporte.");
            response.setIsSuccessful(false);
            response.setErrorMessage(e.getMessage());
            
            // Guardar la conversación con error
            saveConversation(request, response, System.currentTimeMillis() - startTime);
            
            return response;
        }
    }
    
    /**
     * Proporcionar feedback sobre una respuesta del chatbot
     */
    @Transactional
    public ConversationResponse provideFeedback(FeedbackRequest request) {
        ChatbotConversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversación no encontrada con ID: " + request.getConversationId()));
        
        // Actualizar feedback
        conversation.setFeedbackRating(request.getRating());
        conversation.setFeedbackText(request.getComment());
        
        // Si el feedback indica que la respuesta no fue útil, marcar como no exitosa
        if (request.getWasHelpful() != null && !request.getWasHelpful()) {
            conversation.setIsSuccessful(false);
        }
        
        // Si se solicita intervención humana
        if (request.getRequestHumanAgent() != null && request.getRequestHumanAgent()) {
            conversation.setIsHandledByHuman(true);
            conversation.setTransferredAt(LocalDateTime.now());
        }
        
        ChatbotConversation updatedConversation = conversationRepository.save(conversation);
        
        // Si hay feedback negativo, actualizar la tasa de éxito de las entradas de conocimiento utilizadas
        if (request.getRating() != null && request.getRating() <= 2) {
            updateKnowledgeBaseSuccessRates(conversation);
        }
        
        return mapToConversationResponse(updatedConversation);
    }
    
    /**
     * Obtener historial de conversaciones por sesión
     */
    public List<ConversationResponse> getConversationHistory(String sessionId) {
        List<ChatbotConversation> conversations = conversationRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        
        return conversations.stream()
                .map(this::mapToConversationResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener conversaciones paginadas con filtros
     */
    public PageResponse<ConversationResponse> getConversations(Pageable pageable) {
        Page<ChatbotConversation> conversations = conversationRepository.findAll(pageable);
        Page<ConversationResponse> conversationResponses = conversations.map(this::mapToConversationResponse);
        return new PageResponse<>(conversationResponses);
    }
    
    /**
     * Buscar conversaciones por texto
     */
    public PageResponse<ConversationResponse> searchConversations(String text, Pageable pageable) {
        Page<ChatbotConversation> conversations = conversationRepository.searchByText(text, pageable);
        Page<ConversationResponse> conversationResponses = conversations.map(this::mapToConversationResponse);
        return new PageResponse<>(conversationResponses);
    }
    
    /**
     * Guardar una conversación
     */
    private void saveConversation(ChatbotQueryRequest request, ChatbotResponse response, long responseTimeMs) {
        try {
            ChatbotConversation conversation = new ChatbotConversation();
            
            // Datos básicos
            conversation.setSessionId(request.getSessionId());
            conversation.setQuery(request.getQuery());
            conversation.setResponse(response.getResponse());
            conversation.setContext(request.getContext());
            conversation.setIsSuccessful(response.getIsSuccessful());
            conversation.setResponseTimeMs(responseTimeMs);
            
            // Datos del cliente
            conversation.setIpAddress(request.getIpAddress());
            conversation.setUserAgent(request.getUserAgent());
            conversation.setSource(request.getSource());
            
            // Usuario (si está autenticado)
            if (request.getUserId() != null) {
                User user = userRepository.findById(request.getUserId()).orElse(null);
                conversation.setUser(user);
            }
            
            // Metadatos
            conversation.setIntentDetected(response.getIntentDetected());
            conversation.setEntitiesDetected(convertToJson(response.getEntitiesDetected()));
            
            // Referencias a la base de conocimientos
            List<Long> knowledgeBaseIds = response.getReferences().stream()
                    .map(KnowledgeBaseReferenceResponse::getId)
                    .collect(Collectors.toList());
            conversation.setKnowledgeBaseEntriesUsed(convertToJson(knowledgeBaseIds));
            
            // Acciones
            conversation.setActionsTaken(convertToJson(response.getActions()));
            
            // Guardar en la base de datos
            ChatbotConversation savedConversation = conversationRepository.save(conversation);
            
            // Establecer ID en la respuesta
            response.setConversationId(savedConversation.getId());
            
        } catch (Exception e) {
            log.error("Error al guardar conversación: {}", e.getMessage());
        }
    }
    
    /**
     * Actualizar tasas de éxito en la base de conocimientos
     */
    private void updateKnowledgeBaseSuccessRates(ChatbotConversation conversation) {
        try {
            if (conversation.getKnowledgeBaseEntriesUsed() != null) {
                List<Long> entryIds = parseJsonToList(conversation.getKnowledgeBaseEntriesUsed(), Long.class);
                
                for (Long id : entryIds) {
                    // Calcular nueva tasa de éxito (simplificado para el ejemplo)
                    double newRate = 0.5; // Reducir la tasa de éxito en caso de feedback negativo
                    knowledgeService.updateSuccessRate(id, newRate);
                }
            }
        } catch (Exception e) {
            log.error("Error al actualizar tasas de éxito: {}", e.getMessage());
        }
    }
    
    /**
     * Preparar el texto de la consulta
     */
    private String prepareText(String text) {
        if (text == null) {
            return "";
        }
        
        // Normalizar el texto: convertir a minúsculas y eliminar caracteres especiales
        return text.toLowerCase()
                .replaceAll("[^a-záéíóúüñ0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
    
    /**
     * Extraer palabras clave de la consulta
     */
    private List<String> extractKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Dividir el texto en palabras
        String[] words = text.split("\\s+");
        
        // Filtrar palabras vacías y ordenar por longitud (las palabras más largas suelen ser más significativas)
        List<String> keywords = Arrays.stream(words)
                .filter(word -> word.length() > 3) // Ignorar palabras cortas
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .limit(MAX_KEYWORDS)
                .collect(Collectors.toList());
        
        return keywords.isEmpty() ? Arrays.asList(words).subList(0, Math.min(MAX_KEYWORDS, words.length)) : keywords;
    }
    
    /**
     * Buscar entradas relevantes en la base de conocimientos
     */
    private List<KnowledgeBaseEntryResponse> findRelevantEntries(List<String> keywords) {
        if (keywords.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Utilizar las tres primeras palabras clave (si hay suficientes)
        String keyword1 = keywords.size() > 0 ? keywords.get(0) : "";
        String keyword2 = keywords.size() > 1 ? keywords.get(1) : keyword1;
        String keyword3 = keywords.size() > 2 ? keywords.get(2) : keyword2;
        
        return knowledgeService.searchByMultipleKeywords(keyword1, keyword2, keyword3);
    }
    
    /**
     * Convertir objeto a JSON
     */
    private String convertToJson(Object object) {
        if (object == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir objeto a JSON: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Parsear JSON a lista
     */
    private <T> List<T> parseJsonToList(String json, Class<T> elementClass) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            return objectMapper.readValue(json, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (JsonProcessingException e) {
            log.error("Error al parsear JSON a lista: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Parsear JSON a mapa
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error al parsear JSON a mapa: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
    
    /**
     * Mapear entidad a respuesta
     */
    private ConversationResponse mapToConversationResponse(ChatbotConversation conversation) {
        ConversationResponse response = new ConversationResponse();
        
        // Datos básicos
        response.setId(conversation.getId());
        response.setSessionId(conversation.getSessionId());
        response.setQuery(conversation.getQuery());
        response.setResponse(conversation.getResponse());
        response.setContext(conversation.getContext());
        response.setIsSuccessful(conversation.getIsSuccessful());
        response.setFeedbackRating(conversation.getFeedbackRating());
        response.setFeedbackText(conversation.getFeedbackText());
        response.setResponseTimeMs(conversation.getResponseTimeMs());
        response.setIpAddress(conversation.getIpAddress());
        response.setUserAgent(conversation.getUserAgent());
        response.setSource(conversation.getSource());
        response.setIsHandledByHuman(conversation.getIsHandledByHuman());
        response.setHumanAgentId(conversation.getHumanAgentId());
        response.setTransferredAt(conversation.getTransferredAt());
        response.setKnowledgeBaseEntriesUsed(conversation.getKnowledgeBaseEntriesUsed());
        response.setActionsTaken(conversation.getActionsTaken());
        response.setIntentDetected(conversation.getIntentDetected());
        response.setEntitiesDetected(conversation.getEntitiesDetected());
        response.setCreatedAt(conversation.getCreatedAt());
        
        // Usuario
        if (conversation.getUser() != null) {
            response.setUserId(conversation.getUser().getId());
            response.setUserName(conversation.getUser().getEmail());
        }
        
        // Conversión de JSON a mapas para uso en la aplicación
        response.setContextAsMap(parseJsonToMap(conversation.getContext()));
        response.setKnowledgeBaseEntriesAsMap(parseJsonToMap(conversation.getKnowledgeBaseEntriesUsed()));
        response.setActionsAsMap(parseJsonToMap(conversation.getActionsTaken()));
        response.setEntitiesAsMap(parseJsonToMap(conversation.getEntitiesDetected()));
        
        return response;
    }
} 