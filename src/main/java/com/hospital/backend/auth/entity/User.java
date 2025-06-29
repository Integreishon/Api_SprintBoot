// Entidad Usuario para autenticación y autorización
package com.hospital.backend.auth.entity;

import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Entidad Usuario para autenticación y autorización
 * Roles: PATIENT, DOCTOR, SPECIALIST, RECEPTIONIST, ADMIN
 */
@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    
    @Column(name = "dni", nullable = true, unique = true)
    private String dni;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "requires_activation", nullable = false)
    private Boolean requiresActivation = false;
    
}