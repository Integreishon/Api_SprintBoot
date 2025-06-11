package com.hospital.backend.security.service;

import com.hospital.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio de seguridad para validaciones de autorización personalizadas
 */
@Service("securityService")
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    /**
     * Verifica si el usuario actual es el mismo que el ID proporcionado
     */
    public boolean isCurrentUser(Long userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("No hay usuario autenticado");
                return false;
            }

            Object principal = authentication.getPrincipal();
            
            if (!(principal instanceof UserPrincipal)) {
                log.debug("Principal no es UserPrincipal: {}", principal.getClass());
                return false;
            }

            UserPrincipal userPrincipal = (UserPrincipal) principal;
            boolean isCurrentUser = userPrincipal.getId().equals(userId);
            
            log.debug("Verificando si usuario actual {} es igual a {}: {}", 
                    userPrincipal.getId(), userId, isCurrentUser);
            
            return isCurrentUser;
            
        } catch (Exception e) {
            log.error("Error verificando usuario actual: ", e);
            return false;
        }
    }

    /**
     * Verifica si el usuario actual es un doctor específico
     */
    public boolean isCurrentDoctor(Long doctorId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            Object principal = authentication.getPrincipal();
            
            if (!(principal instanceof UserPrincipal)) {
                return false;
            }

            UserPrincipal userPrincipal = (UserPrincipal) principal;
            
            // Verificar que el usuario actual es DOCTOR y que coincide el ID
            boolean isDoctor = userPrincipal.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_DOCTOR"));
            
            if (!isDoctor) {
                log.debug("Usuario actual no es doctor");
                return false;
            }

            // TODO: Aquí deberías obtener el ID del doctor asociado al usuario
            // Por ahora, asumimos que el userId es igual al doctorId
            boolean isCurrentDoctor = userPrincipal.getId().equals(doctorId);
            
            log.debug("Verificando si doctor actual {} es igual a {}: {}", 
                    userPrincipal.getId(), doctorId, isCurrentDoctor);
            
            return isCurrentDoctor;
            
        } catch (Exception e) {
            log.error("Error verificando doctor actual: ", e);
            return false;
        }
    }

    /**
     * Obtiene el ID del usuario actual autenticado
     */
    public Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            Object principal = authentication.getPrincipal();
            
            if (!(principal instanceof UserPrincipal)) {
                return null;
            }

            return ((UserPrincipal) principal).getId();
            
        } catch (Exception e) {
            log.error("Error obteniendo ID de usuario actual: ", e);
            return null;
        }
    }

    /**
     * Verifica si el usuario actual tiene un rol específico
     */
    public boolean hasRole(String role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role.toUpperCase()));
                    
        } catch (Exception e) {
            log.error("Error verificando rol: ", e);
            return false;
        }
    }
}