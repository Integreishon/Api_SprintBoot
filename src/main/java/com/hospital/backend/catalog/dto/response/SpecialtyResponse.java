package com.hospital.backend.catalog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal consultationPrice;
    private BigDecimal discountPercentage;
    private BigDecimal finalPrice; // Precio con descuento aplicado
    private Integer averageDuration;
    private Boolean isActive;
} 