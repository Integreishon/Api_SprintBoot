package com.hospital.backend.config;

import com.hospital.backend.auth.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class CustomAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si no hay autenticación, es un proceso del sistema (o anónimo)
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of("SYSTEM"); // Valor por defecto para usuarios anónimos o sistema
        }

        // Si hay un usuario autenticado, usamos su email o nombre
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return Optional.of(((User) principal).getEmail());
        }

        // Como fallback, usamos el nombre del principal de Spring Security
        return Optional.of(authentication.getName());
    }
} 