// Entidad para horarios de disponibilidad de doctores
package com.hospital.backend.user.entity;

import com.hospital.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "doctor_availability")
@EqualsAndHashCode(callSuper = true)
public class DoctorAvailability extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 1=Lunes, 6=Sábado
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "slot_duration", nullable = false)
    private Integer slotDuration = 30; // minutos
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}