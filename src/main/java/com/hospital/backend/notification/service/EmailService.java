package com.hospital.backend.notification.service;

import com.hospital.backend.notification.entity.Notification;
import com.hospital.backend.user.entity.Patient;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.repository.PatientRepository;
import com.hospital.backend.user.repository.DoctorRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

/**
 * Servicio para el envío de emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.name:Hospital API}")
    private String appName;
    
    /**
     * Enviar notificación por email
     */
    @Async
    public void sendEmailNotification(Notification notification) {
        try {
            String toEmail = notification.getUser().getEmail();
            String subject = notification.getTitle();
            
            // Obtener el nombre del usuario según su rol
            String userName = getUserName(notification.getUser());
            
            // Crear contexto para la plantilla
            Context context = new Context();
            context.setVariable("notification", notification);
            context.setVariable("userName", userName);
            context.setVariable("appName", appName);
            
            // Agregar datos adicionales JSON si existe
            if (notification.getData() != null && !notification.getData().isEmpty()) {
                try {
                    // Aquí podríamos parsear el JSON a un Map, pero por simplicidad lo dejamos como string
                    context.setVariable("additionalData", notification.getData());
                } catch (Exception e) {
                    log.warn("Error al parsear datos adicionales JSON: {}", e.getMessage());
                }
            }
            
            // Seleccionar plantilla basada en el tipo de notificación
            String template = getTemplateByNotificationType(notification);
            
            // Procesar plantilla
            String htmlContent = templateEngine.process(template, context);
            
            // Enviar email
            sendHtmlEmail(toEmail, subject, htmlContent);
            
            log.info("Email enviado a {} con asunto: {}", toEmail, subject);
            
        } catch (Exception e) {
            log.error("Error al enviar email de notificación: {}", e.getMessage());
            throw new RuntimeException("Error al enviar email: " + e.getMessage());
        }
    }
    
    /**
     * Obtener el nombre del usuario según su rol
     */
    private String getUserName(com.hospital.backend.auth.entity.User user) {
        // Intentar obtener el nombre del paciente
        Patient patient = patientRepository.findByUserId(user.getId()).orElse(null);
        if (patient != null) {
            return patient.getFirstName() + " " + patient.getLastName();
        }
        
        // Intentar obtener el nombre del doctor
        Doctor doctor = doctorRepository.findByUserId(user.getId()).orElse(null);
        if (doctor != null) {
            return doctor.getFirstName() + " " + doctor.getLastName();
        }
        
        // Si no se encuentra en ninguna entidad, usar el email
        return user.getEmail();
    }
    
    /**
     * Enviar email HTML
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    
    /**
     * Obtener nombre de plantilla según el tipo de notificación
     */
    private String getTemplateByNotificationType(Notification notification) {
        switch (notification.getType()) {
            case APPOINTMENT_REMINDER:
                return "appointment-reminder";
            case APPOINTMENT_CONFIRMATION:
                return "appointment-confirmation";
            case APPOINTMENT_CANCELLED:
                return "appointment-cancelled";
            case APPOINTMENT_RESCHEDULED:
                return "appointment-rescheduled";
            case PASSWORD_RESET:
                return "password-reset";
            case PRESCRIPTION_CREATED:
                return "prescription-created";
            case LAB_RESULTS_AVAILABLE:
                return "lab-results-available";
            case PAYMENT_RECEIVED:
                return "payment-received";
            case PAYMENT_DUE:
                return "payment-due";
            default:
                // Plantilla genérica para cualquier otro tipo
                return "general-notification";
        }
    }
    
    /**
     * Enviar email de bienvenida
     */
    @Async
    public void sendWelcomeEmail(String to, String name, String activationLink) {
        try {
            String subject = "Bienvenido a " + appName;
            
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("activationLink", activationLink);
            context.setVariable("appName", appName);
            
            String htmlContent = templateEngine.process("welcome-email", context);
            
            sendHtmlEmail(to, subject, htmlContent);
            
            log.info("Email de bienvenida enviado a {}", to);
            
        } catch (Exception e) {
            log.error("Error al enviar email de bienvenida: {}", e.getMessage());
        }
    }
    
    /**
     * Enviar email de restablecimiento de contraseña
     */
    @Async
    public void sendPasswordResetEmail(String to, String name, String resetLink) {
        try {
            String subject = "Restablecimiento de contraseña - " + appName;
            
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("resetLink", resetLink);
            context.setVariable("appName", appName);
            
            String htmlContent = templateEngine.process("password-reset", context);
            
            sendHtmlEmail(to, subject, htmlContent);
            
            log.info("Email de restablecimiento de contraseña enviado a {}", to);
            
        } catch (Exception e) {
            log.error("Error al enviar email de restablecimiento de contraseña: {}", e.getMessage());
        }
    }
} 