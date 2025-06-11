package com.hospital.backend.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.hospital.backend.common.entity.AuditEntity;
import com.hospital.backend.enums.DataType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una configuración del sistema hospitalario.
 * Permite almacenar configuraciones como parámetros del sistema,
 * valores por defecto, y configuraciones personalizables.
 */
@Entity
@Table(name = "hospital_settings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalSetting extends AuditEntity {

    @NotBlank
    @Column(name = "setting_key", unique = true, nullable = false)
    private String key;

    @Column(name = "setting_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    private DataType dataType;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "is_editable", nullable = false)
    private Boolean isEditable;

    /**
     * Verifica si la configuración es una configuración del sistema
     * @return true si es una configuración del sistema
     */
    public boolean isSystemSetting() {
        return !isEditable;
    }
} 