package com.hospital.backend.catalog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeResponse {
    private Long id;
    private String code;
    private String name;
    private String validationPattern;
    private Boolean isActive;
} 