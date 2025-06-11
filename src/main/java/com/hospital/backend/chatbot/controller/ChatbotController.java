package com.hospital.backend.chatbot.controller;

import com.hospital.backend.chatbot.dto.request.ChatbotQueryRequest;
import com.hospital.backend.chatbot.dto.request.FeedbackRequest;
import com.hospital.backend.chatbot.dto.request.KnowledgeBaseEntryRequest;
import com.hospital.backend.chatbot.dto.response.ChatbotResponse;
import com.hospital.backend.chatbot.dto.response.ConversationResponse;
import com.hospital.backend.chatbot.dto.response.KnowledgeBaseEntryResponse;
import com.hospital.backend.chatbot.service.ChatbotKnowledgeService;
import com.hospital.backend.chatbot.service.ChatbotService;
import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador para el chatbot
 */
@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final ChatbotKnowledgeService knowledgeService;
    
    /**
     * Enviar consulta al chatbot
     */
    @PostMapping("/query")
    public ResponseEntity<ApiResponse<ChatbotResponse>> query(
            @Valid @RequestBody ChatbotQueryRequest request,
            HttpServletRequest servletRequest) {
        
        // Configurar datos del cliente
        if (request.getIpAddress() == null) {
            request.setIpAddress(servletRequest.getRemoteAddr());
        }
        
        if (request.getUserAgent() == null) {
            request.setUserAgent(servletRequest.getHeader("User-Agent"));
        }
        
        // Generar ID de sesión si no se proporciona
        if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
            request.setSessionId(UUID.randomUUID().toString());
        }
        
        ChatbotResponse response = chatbotService.processQuery(request);
        
        return ResponseEntity.ok(ApiResponse.success("Consulta procesada exitosamente", response));
    }
    
    /**
     * Obtener historial de conversaciones por sesión
     */
    @GetMapping("/conversations/{sessionId}")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversationHistory(@PathVariable String sessionId) {
        List<ConversationResponse> history = chatbotService.getConversationHistory(sessionId);
        
        return ResponseEntity.ok(ApiResponse.success("Historial de conversaciones obtenido exitosamente", history));
    }
    
    /**
     * Proporcionar feedback sobre una respuesta del chatbot
     */
    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<ConversationResponse>> provideFeedback(@Valid @RequestBody FeedbackRequest request) {
        ConversationResponse response = chatbotService.provideFeedback(request);
        
        return ResponseEntity.ok(ApiResponse.success("Feedback registrado exitosamente", response));
    }
    
    /**
     * Buscar conversaciones por texto
     */
    @GetMapping("/conversations/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ConversationResponse>>> searchConversations(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        PageResponse<ConversationResponse> conversations = chatbotService.searchConversations(text, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Conversaciones encontradas exitosamente", conversations));
    }
    
    /**
     * Obtener preguntas frecuentes
     */
    @GetMapping("/faqs")
    public ResponseEntity<ApiResponse<List<KnowledgeBaseEntryResponse>>> getFAQs(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<KnowledgeBaseEntryResponse> faqs = knowledgeService.getTopFAQs(limit);
        
        return ResponseEntity.ok(ApiResponse.success("FAQs obtenidas exitosamente", faqs));
    }
    
    /**
     * Crear una nueva entrada en la base de conocimientos
     */
    @PostMapping("/knowledge")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<KnowledgeBaseEntryResponse>> createKnowledgeBaseEntry(
            @Valid @RequestBody KnowledgeBaseEntryRequest request) {
        
        KnowledgeBaseEntryResponse entry = knowledgeService.createKnowledgeBaseEntry(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Entrada creada exitosamente", entry));
    }
    
    /**
     * Obtener una entrada de la base de conocimientos por ID
     */
    @GetMapping("/knowledge/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<KnowledgeBaseEntryResponse>> getKnowledgeBaseEntry(@PathVariable Long id) {
        KnowledgeBaseEntryResponse entry = knowledgeService.getKnowledgeBaseEntry(id);
        
        return ResponseEntity.ok(ApiResponse.success("Entrada obtenida exitosamente", entry));
    }
    
    /**
     * Actualizar una entrada de la base de conocimientos
     */
    @PutMapping("/knowledge/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<KnowledgeBaseEntryResponse>> updateKnowledgeBaseEntry(
            @PathVariable Long id,
            @Valid @RequestBody KnowledgeBaseEntryRequest request) {
        
        KnowledgeBaseEntryResponse entry = knowledgeService.updateKnowledgeBaseEntry(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Entrada actualizada exitosamente", entry));
    }
    
    /**
     * Eliminar una entrada de la base de conocimientos
     */
    @DeleteMapping("/knowledge/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteKnowledgeBaseEntry(@PathVariable Long id) {
        knowledgeService.deleteKnowledgeBaseEntry(id);
        
        return ResponseEntity.ok(ApiResponse.success("Entrada eliminada exitosamente", null));
    }
    
    /**
     * Activar/desactivar una entrada de la base de conocimientos
     */
    @PutMapping("/knowledge/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<KnowledgeBaseEntryResponse>> toggleActive(@PathVariable Long id) {
        KnowledgeBaseEntryResponse entry = knowledgeService.toggleActive(id);
        
        String message = entry.getIsActive() ? "Entrada activada exitosamente" : "Entrada desactivada exitosamente";
        
        return ResponseEntity.ok(ApiResponse.success(message, entry));
    }
    
    /**
     * Obtener entradas paginadas con filtros
     */
    @GetMapping("/knowledge")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<PageResponse<KnowledgeBaseEntryResponse>>> getKnowledgeBaseEntries(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PageResponse<KnowledgeBaseEntryResponse> entries = knowledgeService.getKnowledgeBaseEntries(
                isActive, topic, category, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Entradas obtenidas exitosamente", entries));
    }
    
    /**
     * Contar entradas por categoría
     */
    @GetMapping("/knowledge/count-by-category")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countEntriesByCategory() {
        Map<String, Long> counts = knowledgeService.countEntriesByCategory();
        
        return ResponseEntity.ok(ApiResponse.success("Conteo por categoría obtenido exitosamente", counts));
    }
} 