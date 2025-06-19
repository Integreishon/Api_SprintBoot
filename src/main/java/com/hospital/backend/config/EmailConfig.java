package com.hospital.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuración de email para desarrollo y producción
 * En desarrollo, configura un JavaMailSender que no falla si no hay conexión real
 */
@Configuration
public class EmailConfig {

    /**
     * Configuración de JavaMailSender para desarrollo
     * Permite que la aplicación arranque aunque no haya servidor de email real
     */
    @Bean
    @Primary
    JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // Configuración básica (será sobreescrita por application.properties)
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("test@development.com");
        mailSender.setPassword("development_password");
        
        // Propiedades para manejar fallos de conexión graciosamente
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        
        // Timeouts para evitar que la aplicación se cuelgue
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        
        // Para desarrollo: no falla si no hay conexión
        props.put("mail.smtp.ssl.trust", "*");
        
        return mailSender;
    }
}
