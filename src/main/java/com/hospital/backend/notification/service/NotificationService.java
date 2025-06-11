package com.hospital.backend.notification.service;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.repository.UserRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.DeliveryMethod;
import com.hospital.backend.enums.NotificationType;
import com.hospital.backend.notification.dto.request.BulkNotificationRequest;
import com.hospital.backend.notification.dto.request.CreateNotificationRequest;
import com.hospital.backend.notification.dto.request.UpdateNotificationRequest;
import com.hospital.backend.notification.dto.response.NotificationCountResponse;
import com.hospital.backend.notification.dto.response.NotificationResponse;
import com.hospital.backend.notification.entity.Notification;
import com.hospital.backend.notification.repository.NotificationRepository;
import com.hospital.backend.user.entity.Patient;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.repository.PatientRepository;
import com.hospital.backend.user.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de notificaciones
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PushNotificationService pushNotificationService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    /**
     * Crear una nueva notificación
     */
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getUserId()));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setDeliveryMethod(request.getDeliveryMethod());
        notification.setScheduledAt(request.getScheduledAt());
        notification.setRelatedEntityId(request.getRelatedEntityId());
        notification.setRelatedEntityType(request.getRelatedEntityType());
        notification.setActionUrl(request.getActionUrl());
        notification.setActionText(request.getActionText());
        notification.setData(request.getData());
        notification.setIsRead(false);
        notification.setIsSent(false);
        notification.setRetryCount(0);
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Enviar notificación inmediatamente si no está programada
        if (request.getScheduledAt() == null || request.getScheduledAt().isBefore(LocalDateTime.now())) {
            sendNotification(savedNotification);
        }
        
        return mapToNotificationResponse(savedNotification);
    }
    
    /**
     * Crear notificaciones en masa
     */
    @Transactional
    public List<NotificationResponse> createBulkNotifications(BulkNotificationRequest request) {
        List<User> users = userRepository.findAllById(request.getUserIds());
        
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron usuarios con los IDs proporcionados");
        }
        
        List<Notification> notifications = new ArrayList<>();
        
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setType(request.getType());
            notification.setDeliveryMethod(request.getDeliveryMethod());
            notification.setScheduledAt(request.getScheduledAt());
            notification.setRelatedEntityId(request.getRelatedEntityId());
            notification.setRelatedEntityType(request.getRelatedEntityType());
            notification.setActionUrl(request.getActionUrl());
            notification.setActionText(request.getActionText());
            notification.setData(request.getData());
            notification.setIsRead(false);
            notification.setIsSent(false);
            notification.setRetryCount(0);
            
            notifications.add(notification);
        }
        
        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        
        // Enviar notificaciones inmediatamente si no están programadas
        if (request.getScheduledAt() == null || request.getScheduledAt().isBefore(LocalDateTime.now())) {
            sendBulkNotifications(savedNotifications);
        }
        
        return savedNotifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener notificación por ID
     */
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        
        return mapToNotificationResponse(notification);
    }
    
    /**
     * Obtener notificaciones por usuario (paginadas)
     */
    public PageResponse<NotificationResponse> getNotificationsByUserId(Long userId, Pageable pageable) {
        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }
        
        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        Page<NotificationResponse> notificationResponses = notifications.map(this::mapToNotificationResponse);
        return new PageResponse<>(notificationResponses);
    }
    
    /**
     * Obtener notificaciones no leídas por usuario (paginadas)
     */
    public PageResponse<NotificationResponse> getUnreadNotificationsByUserId(Long userId, Pageable pageable) {
        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }
        
        Page<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId, pageable);
        Page<NotificationResponse> notificationResponses = notifications.map(this::mapToNotificationResponse);
        return new PageResponse<>(notificationResponses);
    }
    
    /**
     * Marcar una notificación como leída
     */
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        Notification updatedNotification = notificationRepository.save(notification);
        
        return mapToNotificationResponse(updatedNotification);
    }
    
    /**
     * Marcar todas las notificaciones de un usuario como leídas
     */
    @Transactional
    public int markAllAsReadByUserId(Long userId) {
        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }
        
        return notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
    }
    
    /**
     * Actualizar notificación
     */
    @Transactional
    public NotificationResponse updateNotification(Long id, UpdateNotificationRequest request) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        
        if (request.getIsRead() != null) {
            notification.setIsRead(request.getIsRead());
            if (request.getIsRead()) {
                notification.setReadAt(LocalDateTime.now());
            }
        }
        
        if (request.getActionUrl() != null) {
            notification.setActionUrl(request.getActionUrl());
        }
        
        if (request.getActionText() != null) {
            notification.setActionText(request.getActionText());
        }
        
        Notification updatedNotification = notificationRepository.save(notification);
        
        return mapToNotificationResponse(updatedNotification);
    }
    
    /**
     * Eliminar notificación
     */
    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notificación no encontrada con ID: " + id);
        }
        
        notificationRepository.deleteById(id);
    }
    
    /**
     * Obtener conteo de notificaciones por usuario
     */
    public NotificationCountResponse getNotificationCountsByUserId(Long userId) {
        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }
        
        long totalCount = notificationRepository.count();
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
        long todayCount = notificationRepository.countTodayNotificationsByUserId(userId);
        
        // Calcular el inicio de la semana (domingo)
        LocalDateTime weekStart = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() % 7);
        long thisWeekCount = notificationRepository.countThisWeekNotificationsByUserId(userId, weekStart);
        
        // Contar por tipo
        List<Object[]> countByTypeResult = notificationRepository.countByUserIdGroupByType(userId);
        Map<String, Long> countByType = new HashMap<>();
        for (Object[] result : countByTypeResult) {
            NotificationType type = (NotificationType) result[0];
            Long count = (Long) result[1];
            countByType.put(type.name(), count);
        }
        
        // Contar por método de entrega
        List<Object[]> countByDeliveryMethodResult = notificationRepository.countByUserIdGroupByDeliveryMethod(userId);
        Map<String, Long> countByDeliveryMethod = new HashMap<>();
        for (Object[] result : countByDeliveryMethodResult) {
            DeliveryMethod method = (DeliveryMethod) result[0];
            Long count = (Long) result[1];
            countByDeliveryMethod.put(method.name(), count);
        }
        
        return new NotificationCountResponse(
                totalCount, 
                unreadCount, 
                todayCount, 
                thisWeekCount, 
                countByType, 
                countByDeliveryMethod
        );
    }
    
    /**
     * Enviar notificación basada en su método de entrega
     */
    @Async
    public void sendNotification(Notification notification) {
        try {
            switch (notification.getDeliveryMethod()) {
                case EMAIL:
                    emailService.sendEmailNotification(notification);
                    break;
                case PUSH:
                    pushNotificationService.sendPushNotification(notification);
                    break;
                case IN_APP:
                    // Las notificaciones in-app se muestran automáticamente 
                    // cuando el usuario las consulta, no requieren envío adicional
                    break;
                case SMS:
                    // Aquí iría la implementación del envío por SMS
                    // Por ahora, se marca como enviado
                    break;
                case WHATSAPP:
                    // Aquí iría la implementación del envío por WhatsApp
                    // Por ahora, se marca como enviado
                    break;
                default:
                    log.warn("Método de entrega no soportado: {}", notification.getDeliveryMethod());
                    break;
            }
            
            // Marcar como enviada
            notification.setIsSent(true);
            notification.setSentAt(LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("Error al enviar notificación ID {}: {}", notification.getId(), e.getMessage());
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
        }
        
        notificationRepository.save(notification);
    }
    
    /**
     * Enviar notificaciones en masa
     */
    @Async
    public void sendBulkNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            sendNotification(notification);
        }
    }
    
    /**
     * Procesar notificaciones programadas
     */
    @Async
    @Transactional
    public void processScheduledNotifications(int maxRetries) {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> notificationsToSend = notificationRepository.findScheduledNotificationsToSend(now, maxRetries);
        
        for (Notification notification : notificationsToSend) {
            sendNotification(notification);
        }
    }
    
    /**
     * Obtener el nombre del usuario según su rol
     */
    private String getUserName(User user) {
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
     * Mapear entidad Notification a DTO NotificationResponse
     */
    private NotificationResponse mapToNotificationResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        
        // Usuario
        response.setUserId(notification.getUser().getId());
        response.setUserName(getUserName(notification.getUser()));
        response.setUserEmail(notification.getUser().getEmail());
        
        // Contenido
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        
        // Metadatos
        response.setType(notification.getType());
        response.setTypeName(notification.getType().getDisplayName());
        response.setDeliveryMethod(notification.getDeliveryMethod());
        response.setDeliveryMethodName(notification.getDeliveryMethod().getDisplayName());
        
        // Estado
        response.setIsRead(notification.getIsRead());
        response.setReadAt(notification.getReadAt());
        response.setIsSent(notification.getIsSent());
        response.setSentAt(notification.getSentAt());
        response.setRetryCount(notification.getRetryCount());
        response.setErrorMessage(notification.getErrorMessage());
        response.setScheduledAt(notification.getScheduledAt());
        
        // Referencias
        response.setRelatedEntityId(notification.getRelatedEntityId());
        response.setRelatedEntityType(notification.getRelatedEntityType());
        
        // Acciones
        response.setActionUrl(notification.getActionUrl());
        response.setActionText(notification.getActionText());
        
        // Datos adicionales
        response.setData(notification.getData());
        
        // Auditoría
        response.setCreatedAt(notification.getCreatedAt());
        response.setCreatedBy(notification.getCreatedBy());
        
        // Utilidad para cliente
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        response.setIsNew(!notification.getIsRead() && notification.getCreatedAt().isAfter(yesterday));
        
        // Calcular tiempo transcurrido en minutos
        if (notification.getCreatedAt() != null) {
            response.setTimeAgo(ChronoUnit.MINUTES.between(notification.getCreatedAt(), now));
        }
        
        return response;
    }
} 