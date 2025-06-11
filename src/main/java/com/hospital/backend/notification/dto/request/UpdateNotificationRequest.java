package com.hospital.backend.notification.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de actualización de una notificación existente
 * (permite marcar como leída o actualizar los metadatos)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationRequest {

    private Boolean isRead;
    
    @Size(max = 500, message = "La URL de acción no debe exceder los 500 caracteres")
    private String actionUrl;
    
    @Size(max = 100, message = "El texto de acción no debe exceder los 100 caracteres")
    private String actionText;
} 