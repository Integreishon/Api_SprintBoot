package com.hospital.backend.analytics.service;

import com.hospital.backend.analytics.entity.AuditLog;
import com.hospital.backend.analytics.repository.AuditLogRepository;
import com.hospital.backend.enums.OperationType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Servicio para registrar auditorías de acciones en el sistema
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    
    /**
     * Registra una acción en el sistema con el usuario actual
     */
    public AuditLog logAction(OperationType operationType, String entityName, Long entityId, String description) {
        AuditLog auditLog = new AuditLog();
        
        // Información del usuario actual
        setUserInfo(auditLog);
        
        // Información de la operación
        auditLog.setOperationType(operationType);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        auditLog.setSuccessful(true);
        
        // Información de la solicitud HTTP
        setRequestInfo(auditLog);
        
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Registra un error en el sistema
     */
    public AuditLog logError(OperationType operationType, String entityName, Long entityId, 
                          String description, String errorMessage) {
        AuditLog auditLog = new AuditLog();
        
        // Información del usuario actual
        setUserInfo(auditLog);
        
        // Información de la operación
        auditLog.setOperationType(operationType);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        auditLog.setSuccessful(false);
        auditLog.setErrorMessage(errorMessage);
        
        // Información de la solicitud HTTP
        setRequestInfo(auditLog);
        
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Registra una acción de login exitosa
     */
    public AuditLog logLogin(Long userId, String username) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setUsername(username);
        auditLog.setOperationType(OperationType.LOGIN);
        auditLog.setEntityName("User");
        auditLog.setEntityId(userId);
        auditLog.setDescription("Inicio de sesión exitoso");
        auditLog.setSuccessful(true);
        
        // Información de la solicitud HTTP
        setRequestInfo(auditLog);
        
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Registra un intento fallido de login
     */
    public AuditLog logFailedLogin(String username, String errorMessage) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUsername(username);
        auditLog.setOperationType(OperationType.LOGIN);
        auditLog.setEntityName("User");
        auditLog.setDescription("Intento fallido de inicio de sesión");
        auditLog.setSuccessful(false);
        auditLog.setErrorMessage(errorMessage);
        
        // Información de la solicitud HTTP
        setRequestInfo(auditLog);
        
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Registra una acción con tiempo de ejecución
     */
    public AuditLog logActionWithExecutionTime(OperationType operationType, String entityName, 
                                           Long entityId, String description, long executionTimeMs) {
        AuditLog auditLog = logAction(operationType, entityName, entityId, description);
        auditLog.setExecutionTimeMs(executionTimeMs);
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Establece la información del usuario actual en el log
     */
    private void setUserInfo(AuditLog auditLog) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
            // Obtener información del usuario autenticado
            try {
                // UserPrincipal o CustomUserDetails según la implementación
                Object principal = authentication.getPrincipal();
                
                // Usar reflection para obtener user ID y username de forma segura
                // Este código debe adaptarse según la implementación específica
                Long userId = null;
                String username = null;
                
                // Intenta obtener el ID de usuario si está disponible
                try {
                    if (principal.getClass().getMethod("getId") != null) {
                        userId = (Long) principal.getClass().getMethod("getId").invoke(principal);
                    }
                } catch (Exception e) {
                    // Método no disponible, intentar con username
                }
                
                // Obtener el username
                username = authentication.getName();
                
                auditLog.setUserId(userId);
                auditLog.setUsername(username);
            } catch (Exception e) {
                // Si hay algún error, usar solo el nombre como cadena
                auditLog.setUsername(authentication.getName());
            }
        } else {
            auditLog.setUsername("anonymous");
        }
    }
    
    /**
     * Establece la información de la solicitud HTTP en el log
     */
    private void setRequestInfo(AuditLog auditLog) {
        try {
            ServletRequestAttributes requestAttributes = 
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                
                auditLog.setUserIp(getClientIp(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
                auditLog.setRequestUrl(request.getRequestURI());
                auditLog.setRequestMethod(request.getMethod());
                auditLog.setSessionId(request.getSession().getId());
                
                // Agregar parámetros de la solicitud (opcional)
                StringBuilder params = new StringBuilder();
                request.getParameterMap().forEach((key, value) -> {
                    if (!key.toLowerCase().contains("password") && !key.toLowerCase().contains("token")) {
                        params.append(key).append("=").append(String.join(",", value)).append(";");
                    }
                });
                auditLog.setRequestParams(params.toString());
            }
        } catch (Exception e) {
            // Si no hay contexto de solicitud disponible o hay un error
        }
    }
    
    /**
     * Obtiene la IP real del cliente, considerando proxies
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // Si hay múltiples IPs, tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
} 