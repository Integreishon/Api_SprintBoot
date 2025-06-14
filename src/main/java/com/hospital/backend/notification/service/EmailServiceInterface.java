package com.hospital.backend.notification.service;

import com.hospital.backend.notification.entity.Notification;

/**
 * Interfaz para servicios de email
 */
public interface EmailServiceInterface {
    
    void sendEmailNotification(Notification notification);
    
    void sendWelcomeEmail(String to, String name, String activationLink);
    
    void sendPasswordResetEmail(String to, String name, String resetLink);
}
