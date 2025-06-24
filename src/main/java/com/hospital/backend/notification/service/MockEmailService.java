package com.hospital.backend.notification.service;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.enums.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Servicio MOCK para emails cuando el email está deshabilitado
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "spring.mail.host", matchIfMissing = true, havingValue = "disabled")
public class MockEmailService implements EmailServiceInterface {
    
    @Override
    @Async
    public void sendEmail(User user, String subject, NotificationType type, Map<String, Object> data) {
        log.info("📧 MOCK EMAIL: Enviando email tipo {} a {} - Asunto: {}", 
                 type, user.getEmail(), subject);
        log.debug("📧 MOCK EMAIL: Datos: {}", data);
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
