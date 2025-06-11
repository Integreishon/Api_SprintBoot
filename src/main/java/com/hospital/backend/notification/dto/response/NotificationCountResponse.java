package com.hospital.backend.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO para la respuesta del conteo de notificaciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCountResponse {

    private Long totalCount;
    private Long unreadCount;
    private Long todayCount;
    private Long thisWeekCount;
    
    // Conteo por tipo de notificación
    private Map<String, Long> countByType;
    
    // Conteo por método de entrega
    private Map<String, Long> countByDeliveryMethod;
} 