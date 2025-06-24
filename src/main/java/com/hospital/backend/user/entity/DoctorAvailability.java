// Entidad para horarios de disponibilidad de doctores
package com.hospital.backend.user.entity;

import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.TimeBlock;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidad para bloques de disponibilidad de doctores
 * Usa bloques de tiempo en lugar de horarios específicos
 */
@Data
@Entity
@Table(name = "doctor_availability")
@EqualsAndHashCode(callSuper = true)
public class DoctorAvailability extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 1=Lunes, 7=Domingo
    
    @Enumerated(EnumType.STRING)
    @Column(name = "time_block", nullable = false)
    private TimeBlock timeBlock; // MORNING, AFTERNOON, FULL_DAY
    
    @Column(name = "max_patients", nullable = false)
    private Integer maxPatients = 20;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
}