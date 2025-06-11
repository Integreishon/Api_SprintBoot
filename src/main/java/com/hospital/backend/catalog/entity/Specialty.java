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

@Entity
@Table(name = "specialties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Specialty extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "consultation_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal consultationPrice;
    
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;
    
    @Column(name = "average_duration", nullable = false)
    private Integer averageDuration = 30; // Duración en minutos
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Método para calcular precio final con descuento
    public BigDecimal getFinalPrice() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return consultationPrice;
        }
        
        BigDecimal discountFactor = BigDecimal.ONE.subtract(
                discountPercentage.divide(new BigDecimal("100")));
        
        return consultationPrice.multiply(discountFactor).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
