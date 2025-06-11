package com.hospital.backend.chatbot.dto.request;

import com.hospital.backend.enums.DataType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de creación/actualización de una entrada en la base de conocimientos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseEntryRequest {

    @NotBlank(message = "El tema es obligatorio")
    @Size(max = 100, message = "El tema no debe exceder los 100 caracteres")
    private String topic;
    
    @NotBlank(message = "La pregunta es obligatoria")
    @Size(max = 500, message = "La pregunta no debe exceder los 500 caracteres")
    private String question;
    
    @NotBlank(message = "La respuesta es obligatoria")
    @Size(max = 5000, message = "La respuesta no debe exceder los 5000 caracteres")
    private String answer;
    
    @NotBlank(message = "Las palabras clave son obligatorias")
    @Size(max = 500, message = "Las palabras clave no deben exceder los 500 caracteres")
    private String keywords;
    
    private DataType dataType = DataType.STRING;
    
    private Boolean isActive = true;
    
    @Min(value = 1, message = "La prioridad debe ser al menos 1")
    @Max(value = 10, message = "La prioridad no debe exceder 10")
    private Integer priority = 5;
    
    @Size(max = 100, message = "La categoría no debe exceder los 100 caracteres")
    private String category;
    
    @Size(max = 100, message = "La subcategoría no debe exceder los 100 caracteres")
    private String subcategory;
    
    @Size(max = 255, message = "La referencia externa no debe exceder los 255 caracteres")
    private String externalReference;
    
    @Size(max = 100, message = "El tipo de entidad relacionada no debe exceder los 100 caracteres")
    private String relatedEntityType;
    
    private Long relatedEntityId;
    
    @Size(max = 5000, message = "Los metadatos no deben exceder los 5000 caracteres")
    private String metadata;
} 