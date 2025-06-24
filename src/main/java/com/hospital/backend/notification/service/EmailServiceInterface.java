package com.hospital.backend.notification.service;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.enums.NotificationType;

import java.util.Map;

/**
 * Interfaz para servicios de email
 * Adaptada para la nueva lógica sin entidad Notification
 */
public interface EmailServiceInterface {
    
    void sendEmail(User user, String subject, NotificationType type, Map<String, Object> data);
    
    void sendWelcomeEmail(String to, String name, String activationLink);
    
    void sendPasswordResetEmail(String to, String name, String resetLink);
}
