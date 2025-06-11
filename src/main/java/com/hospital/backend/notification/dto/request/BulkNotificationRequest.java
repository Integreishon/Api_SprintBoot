package com.hospital.backend.notification.dto.request;

import com.hospital.backend.enums.DeliveryMethod;
import com.hospital.backend.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la solicitud de envío de notificaciones a múltiples usuarios
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkNotificationRequest {

    @NotEmpty(message = "La lista de IDs de usuarios no puede estar vacía")
    private List<Long> userIds;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 255, message = "El título debe tener entre 3 y 255 caracteres")
    private String title;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 3, max = 2000, message = "El mensaje debe tener entre 3 y 2000 caracteres")
    private String message;

    @NotNull(message = "El tipo de notificación es obligatorio")
    private NotificationType type;

    @NotNull(message = "El método de entrega es obligatorio")
    private DeliveryMethod deliveryMethod;

    private LocalDateTime scheduledAt;

    private Long relatedEntityId;

    private String relatedEntityType;

    @Size(max = 500, message = "La URL de acción no debe exceder los 500 caracteres")
    private String actionUrl;

    @Size(max = 100, message = "El texto de acción no debe exceder los 100 caracteres")
    private String actionText;

    @Size(max = 5000, message = "Los datos adicionales no deben exceder los 5000 caracteres")
    private String data;
} 