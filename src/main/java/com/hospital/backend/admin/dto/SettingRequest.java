package com.hospital.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.hospital.backend.enums.DataType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de creación o actualización de configuraciones
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingRequest {

    @NotBlank(message = "La clave de configuración no puede estar vacía")
    private String key;

    @NotNull(message = "El valor de configuración no puede ser nulo")
    private String value;

    private DataType dataType;

    private String description;

    private String category;

    private Boolean isPublic;

    private Boolean isEditable;
} 