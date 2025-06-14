package com.hospital.backend.notification.service;

import com.hospital.backend.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio MOCK para emails cuando el email está deshabilitado
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "spring.mail.host", matchIfMissing = true, havingValue = "disabled")
public class MockEmailService implements EmailServiceInterface {
    
    @Override
    @Async
    public void sendEmailNotification(Notification notification) {
        log.info("📧 MOCK EMAIL: Enviando notificación a {} - Asunto: {}", 
                 notification.getUser().getEmail(), 
                 notification.getTitle());
        log.debug("📧 MOCK EMAIL: Contenido: {}", notification.getMessage());
        // No hace nada real - solo logging
    }
    
    @Override
    @Async
    public void sendWelcomeEmail(String to, String name, String activationLink) {
        log.info("📧 MOCK EMAIL: Enviando email de bienvenida a {} para {}", to, name);
        // No hace nada real - solo logging
    }
    
    @Override
    @Async
    public void sendPasswordResetEmail(String to, String name, String resetLink) {
        log.info("📧 MOCK EMAIL: Enviando email de restablecimiento a {} para {}", to, name);
        // No hace nada real - solo logging
    }
}
