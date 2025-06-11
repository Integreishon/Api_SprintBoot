package com.hospital.backend.notification.dto.response;

import com.hospital.backend.enums.DeliveryMethod;
import com.hospital.backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de una notificación
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    
    // Usuario
    private Long userId;
    private String userName;
    private String userEmail;
    
    // Contenido de la notificación
    private String title;
    private String message;
    
    // Metadatos
    private NotificationType type;
    private String typeName;
    private DeliveryMethod deliveryMethod;
    private String deliveryMethodName;
    
    // Estado
    private Boolean isRead;
    private LocalDateTime readAt;
    private Boolean isSent;
    private LocalDateTime sentAt;
    private Integer retryCount;
    private String errorMessage;
    private LocalDateTime scheduledAt;
    
    // Referencias
    private Long relatedEntityId;
    private String relatedEntityType;
    
    // Acciones
    private String actionUrl;
    private String actionText;
    
    // Datos adicionales
    private String data;
    
    // Auditoría
    private LocalDateTime createdAt;
    private String createdBy;
    
    // Utilidad para cliente
    private Boolean isNew; // Si es nueva (<24h y no leída)
    private Long timeAgo; // Tiempo en minutos desde que se creó
} 