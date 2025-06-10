// Repository para gestión de usuarios
package com.hospital.backend.auth.repository;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);
    
    boolean existsByEmail(String email);
    
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = :isActive")
    Page<User> findByRoleAndIsActive(@Param("role") UserRole role, 
                                   @Param("isActive") Boolean isActive, 
                                   Pageable pageable);
    
    long countByRole(UserRole role);
    
    long countByIsActive(Boolean isActive);
    
}