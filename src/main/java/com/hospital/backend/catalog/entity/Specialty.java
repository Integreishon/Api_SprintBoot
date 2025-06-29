package com.hospital.backend.catalog.entity;

import com.hospital.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Especialidades médicas
 * Incluye categorización Urovital (primaria/derivación)
 */
@Entity
@Table(name = "specialties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Specialty extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "consultation_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal consultationPrice;
    
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;
    
    @Column(name = "average_duration", nullable = false)
    private Integer averageDuration = 30;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false; // Solo urología
    
    @Column(name = "requires_referral", nullable = false)
    private Boolean requiresReferral = false; // Especialidades bajo demanda
    
    // Método para calcular precio final con descuento
    public BigDecimal getFinalPrice() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return consultationPrice;
        }
        
        BigDecimal discountFactor = BigDecimal.ONE.subtract(
                discountPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        
        return consultationPrice.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }
}
