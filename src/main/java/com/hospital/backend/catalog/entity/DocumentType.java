package com.hospital.backend.catalog.entity;

import com.hospital.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentType extends BaseEntity {

    @Column(name = "code", length = 10, nullable = false, unique = true)
    private String code;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "validation_pattern", length = 100)
    private String validationPattern;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
