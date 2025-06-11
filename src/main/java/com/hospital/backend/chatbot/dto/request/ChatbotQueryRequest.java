package com.hospital.backend.chatbot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de consulta al chatbot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotQueryRequest {

    @NotBlank(message = "La consulta es obligatoria")
    @Size(min = 2, max = 1000, message = "La consulta debe tener entre 2 y 1000 caracteres")
    private String query;
    
    private Long userId; // Opcional, si el usuario está autenticado
    
    @NotBlank(message = "El ID de sesión es obligatorio")
    private String sessionId;
    
    private String context; // Contexto de la conversación en formato JSON
    
    private String language = "es"; // Idioma de la consulta
    
    private Boolean includeReferences = true; // Si se deben incluir referencias en la respuesta
    
    private String source; // Fuente de la consulta (web, mobile, etc.)
    
    private String ipAddress; // Dirección IP del cliente
    
    private String userAgent; // User-Agent del cliente
}
