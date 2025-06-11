package com.hospital.backend.catalog.entity;

import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.PaymentMethodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentMethodType type;
    
    @Column(name = "processing_fee", precision = 5, scale = 2)
    private BigDecimal processingFee; // Comisión en porcentaje
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "integration_code", length = 50)
    private String integrationCode; // Código para integración con pasarelas de pago
}
