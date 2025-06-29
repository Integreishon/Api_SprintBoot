// Entidad para relación muchos-a-muchos entre doctores y especialidades
package com.hospital.backend.user.entity;

import com.hospital.backend.catalog.entity.Specialty;
import com.hospital.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "doctor_specialties",
       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "specialty_id"}))
@EqualsAndHashCode(callSuper = true)
public class DoctorSpecialty extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;
    
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
    
    @Column(name = "certification_date")
    private LocalDate certificationDate;
}