package com.hospital.backend.analytics.controller;

import com.hospital.backend.analytics.dto.AuditLogResponse;
import com.hospital.backend.analytics.entity.AuditLog;
import com.hospital.backend.analytics.repository.AuditLogRepository;
import com.hospital.backend.analytics.service.AuditService;
import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.enums.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para acceso a logs de auditoría
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;
    private final AuditService auditService;
    
    /**
     * Obtiene logs de auditoría paginados
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogs(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<AuditLog> auditLogs = auditLogRepository.findAll(pageable);
        
        // Registrar la acción de consulta en la auditoría
        auditService.logAction(
                OperationType.ACCESS,
                "AuditLog",
                null,
                "Consulta de logs de auditoría"
        );
        
        // Convertir entidades a DTOs
        List<AuditLogResponse> auditLogResponses = auditLogs.getContent().stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
        
        // Crear respuesta paginada
        Page<AuditLogResponse> pageDto = new PageImpl<>(auditLogResponses, auditLogs.getPageable(), auditLogs.getTotalElements());
        PageResponse<AuditLogResponse> pageResponse = new PageResponse<>(pageDto);
        
        return ResponseEntity.ok(ApiResponse.success("Logs de auditoría obtenidos exitosamente", pageResponse));
    }
    
    /**
     * Busca logs de auditoría por tipo de operación
     */
    @GetMapping("/operation/{operationType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogsByOperationType(
            @PathVariable OperationType operationType,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<AuditLog> auditLogs = auditLogRepository.findByOperationType(operationType, pageable);
        
        // Convertir entidades a DTOs
        List<AuditLogResponse> auditLogResponses = auditLogs.getContent().stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
        
        // Crear respuesta paginada
        Page<AuditLogResponse> pageDto = new PageImpl<>(auditLogResponses, auditLogs.getPageable(), auditLogs.getTotalElements());
        PageResponse<AuditLogResponse> pageResponse = new PageResponse<>(pageDto);
        
        return ResponseEntity.ok(ApiResponse.success("Logs de auditoría filtrados por operación", pageResponse));
    }
    
    /**
     * Busca logs de auditoría por usuario
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<AuditLog> auditLogs = auditLogRepository.findByUserId(userId, pageable);
        
        // Convertir entidades a DTOs
        List<AuditLogResponse> auditLogResponses = auditLogs.getContent().stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
        
        // Crear respuesta paginada
        Page<AuditLogResponse> pageDto = new PageImpl<>(auditLogResponses, auditLogs.getPageable(), auditLogs.getTotalElements());
        PageResponse<AuditLogResponse> pageResponse = new PageResponse<>(pageDto);
        
        return ResponseEntity.ok(ApiResponse.success("Logs de auditoría filtrados por usuario", pageResponse));
    }
    
    /**
     * Busca logs de auditoría por rango de fechas
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<AuditLog> auditLogs = auditLogRepository.findByDateRange(startDate, endDate, pageable);
        
        // Convertir entidades a DTOs
        List<AuditLogResponse> auditLogResponses = auditLogs.getContent().stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
        
        // Crear respuesta paginada
        Page<AuditLogResponse> pageDto = new PageImpl<>(auditLogResponses, auditLogs.getPageable(), auditLogs.getTotalElements());
        PageResponse<AuditLogResponse> pageResponse = new PageResponse<>(pageDto);
        
        return ResponseEntity.ok(ApiResponse.success("Logs de auditoría filtrados por rango de fechas", pageResponse));
    }
    
    /**
     * Busca logs de auditoría por éxito o fracaso
     */
    @GetMapping("/status/{successful}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogsByStatus(
            @PathVariable Boolean successful,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<AuditLog> auditLogs = auditLogRepository.findBySuccessful(successful, pageable);
        
        // Convertir entidades a DTOs
        List<AuditLogResponse> auditLogResponses = auditLogs.getContent().stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
        
        // Crear respuesta paginada
        Page<AuditLogResponse> pageDto = new PageImpl<>(auditLogResponses, auditLogs.getPageable(), auditLogs.getTotalElements());
        PageResponse<AuditLogResponse> pageResponse = new PageResponse<>(pageDto);
        
        return ResponseEntity.ok(ApiResponse.success(
                successful ? "Logs de auditoría exitosos" : "Logs de auditoría con errores", 
                pageResponse
        ));
    }
    
    /**
     * Búsqueda avanzada de logs de auditoría
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> searchAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) OperationType operationType,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Boolean successful,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<AuditLog> auditLogs = auditLogRepository.search(
                userId, operationType, entityName, entityId, module, successful, startDate, endDate, pageable);
        
        // Convertir entidades a DTOs
        List<AuditLogResponse> auditLogResponses = auditLogs.getContent().stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
        
        // Crear respuesta paginada
        Page<AuditLogResponse> pageDto = new PageImpl<>(auditLogResponses, auditLogs.getPageable(), auditLogs.getTotalElements());
        PageResponse<AuditLogResponse> pageResponse = new PageResponse<>(pageDto);
        
        return ResponseEntity.ok(ApiResponse.success("Búsqueda avanzada de logs de auditoría", pageResponse));
    }
    
    /**
     * Mapea la entidad AuditLog al DTO AuditLogResponse
     */
    private AuditLogResponse mapToAuditLogResponse(AuditLog auditLog) {
        AuditLogResponse response = AuditLogResponse.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .username(auditLog.getUsername())
                .userIp(auditLog.getUserIp())
                .operationType(auditLog.getOperationType())
                .operationTypeName(auditLog.getOperationType() != null ? auditLog.getOperationType().getDisplayName() : null)
                .entityName(auditLog.getEntityName())
                .entityId(auditLog.getEntityId())
                .description(auditLog.getDescription())
                .requestUrl(auditLog.getRequestUrl())
                .requestMethod(auditLog.getRequestMethod())
                .successful(auditLog.getSuccessful())
                .errorMessage(auditLog.getErrorMessage())
                .executionTimeMs(auditLog.getExecutionTimeMs())
                .module(auditLog.getModule())
                .action(auditLog.getAction())
                .createdAt(auditLog.getCreatedAt())
                .build();
        
        // Configurar etiquetas de UI
        response.setUILabels();
        
        return response;
    }
} 