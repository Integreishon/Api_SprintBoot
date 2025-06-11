package com.hospital.backend.chatbot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de feedback sobre una respuesta del chatbot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    @NotNull(message = "El ID de la conversación es obligatorio")
    private Long conversationId;
    
    @Min(value = 1, message = "La calificación debe ser al menos 1")
    @Max(value = 5, message = "La calificación no debe exceder 5")
    private Integer rating;
    
    @Size(max = 1000, message = "El comentario no debe exceder los 1000 caracteres")
    private String comment;
    
    private Boolean wasHelpful;
    
    private Boolean requestHumanAgent;
} 