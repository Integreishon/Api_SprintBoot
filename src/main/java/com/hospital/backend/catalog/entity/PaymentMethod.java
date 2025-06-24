package com.hospital.backend.catalog.entity;

import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.PaymentMethodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Métodos de pago
 * Adaptados para métodos locales peruanos
 */
@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod extends BaseEntity {

    @Column(name = "name", length = 50, nullable = false)
    private String name; // "Efectivo", "Yape", "Plin"
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentMethodType type; // CASH, DIGITAL, CARD
    
    @Column(name = "processing_fee", precision = 5, scale = 2)
    private BigDecimal processingFee = BigDecimal.ZERO; // Comisión en porcentaje
    
    @Column(name = "integration_code", length = 100)
    private String integrationCode;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "requires_manual_validation", nullable = false)
    private Boolean requiresManualValidation = false;
    
    @Column(name = "is_digital", nullable = false)
    private Boolean isDigital = false;
}
