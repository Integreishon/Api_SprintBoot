package com.hospital.backend.analytics.entity;

import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad para el registro de auditoría de acciones en el sistema
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username", length = 100)
    private String username;
    
    @Column(name = "user_ip", length = 50)
    private String userIp;
    
    @Column(name = "user_agent", length = 255)
    private String userAgent;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;
    
    @Column(name = "entity_name", length = 100, nullable = false)
    private String entityName;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "request_url", length = 255)
    private String requestUrl;
    
    @Column(name = "request_method", length = 10)
    private String requestMethod;
    
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;
    
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;
    
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
    
    @Column(name = "status", nullable = false)
    private Boolean successful = true;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "module", length = 50)
    private String module;
    
    @Column(name = "action", length = 100)
    private String action;
    
    // Método conveniente para crear un log de auditoría básico
    public static AuditLog createLog(Long userId, String username, OperationType operationType, 
                                    String entityName, Long entityId, String description) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperationType(operationType);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setSuccessful(true);
        return log;
    }
    
    // Método para registrar un error
    public void markAsFailed(String errorMessage) {
        this.successful = false;
        this.errorMessage = errorMessage;
    }
} 