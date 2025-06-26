// Servicio para gestión de usuarios
package com.hospital.backend.auth.service;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.repository.UserRepository;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
    }
    
    public User findByPatientDocumentNumber(String documentNumber) {
        return userRepository.findByPatientDocumentNumber(documentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "documentNumber", documentNumber));
    }
    
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Page<User> findByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }
    
    public Page<User> findByIsActive(Boolean isActive, Pageable pageable) {
        return userRepository.findByIsActive(isActive, pageable);
    }
    
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User user = findById(id);
        
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        user.setIsActive(updatedUser.getIsActive());
        user.setRequiresActivation(updatedUser.getRequiresActivation());
        
        log.info("Usuario {} actualizado", user.getEmail() != null ? user.getEmail() : "sin email");
        return userRepository.save(user);
    }
    
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId);
        
        // Si el usuario no tiene contraseña (creado en recepción), no verificar la contraseña actual
        if (user.getPasswordHash() != null) {
            if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                throw new IllegalArgumentException("Contraseña actual incorrecta");
            }
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        // Si el usuario tenía requires_activation en true, actualizarlo a false
        if (user.getRequiresActivation() != null && user.getRequiresActivation()) {
            user.setRequiresActivation(false);
        }
        userRepository.save(user);
        
        log.info("Contraseña cambiada para usuario {}", user.getEmail() != null ? user.getEmail() : "sin email");
    }
    
    @Transactional
    public void activateUser(Long id) {
        User user = findById(id);
        user.setIsActive(true);
        userRepository.save(user);
        
        log.info("Usuario {} activado", user.getEmail() != null ? user.getEmail() : "sin email");
    }
    
    @Transactional
    public void deactivateUser(Long id) {
        User user = findById(id);
        user.setIsActive(false);
        userRepository.save(user);
        
        log.info("Usuario {} desactivado", user.getEmail() != null ? user.getEmail() : "sin email");
    }
    
    @Transactional
    public User createUserWithoutCredentials(UserRole role) {
        User user = new User();
        user.setRole(role);
        user.setIsActive(true);
        user.setRequiresActivation(true);
        
        log.info("Usuario creado sin credenciales con rol {}", role);
        return userRepository.save(user);
    }
    
    public long countByRole(UserRole role) {
        return userRepository.countByRole(role);
    }
    
    public long countActiveUsers() {
        return userRepository.countByIsActive(true);
    }
    
    public long countUsersRequiringActivation() {
        return userRepository.countByRequiresActivation();
    }
}