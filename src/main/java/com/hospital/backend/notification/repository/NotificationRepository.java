package com.hospital.backend.notification.repository;

import com.hospital.backend.enums.DeliveryMethod;
import com.hospital.backend.enums.NotificationType;
import com.hospital.backend.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones con notificaciones
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Buscar notificaciones por usuario
     */
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Buscar notificaciones no leídas por usuario
     */
    Page<Notification> findByUserIdAndIsReadFalse(Long userId, Pageable pageable);
    
    /**
     * Buscar notificaciones por tipo
     */
    Page<Notification> findByType(NotificationType type, Pageable pageable);
    
    /**
     * Buscar notificaciones por método de entrega
     */
    Page<Notification> findByDeliveryMethod(DeliveryMethod deliveryMethod, Pageable pageable);
    
    /**
     * Buscar notificaciones por usuario y tipo
     */
    Page<Notification> findByUserIdAndType(Long userId, NotificationType type, Pageable pageable);
    
    /**
     * Buscar notificaciones por usuario y método de entrega
     */
    Page<Notification> findByUserIdAndDeliveryMethod(Long userId, DeliveryMethod deliveryMethod, Pageable pageable);
    
    /**
     * Buscar notificaciones por rango de fechas de creación
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate")
    Page<Notification> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Buscar notificaciones programadas para envío
     */
    @Query("SELECT n FROM Notification n WHERE n.scheduledAt <= :now AND n.isSent = false AND (n.retryCount < :maxRetries OR :maxRetries = 0)")
    List<Notification> findScheduledNotificationsToSend(
            @Param("now") LocalDateTime now,
            @Param("maxRetries") Integer maxRetries);
    
    /**
     * Buscar notificaciones por entidad relacionada
     */
    Page<Notification> findByRelatedEntityTypeAndRelatedEntityId(
            String relatedEntityType, Long relatedEntityId, Pageable pageable);
    
    /**
     * Contar notificaciones no leídas por usuario
     */
    long countByUserIdAndIsReadFalse(Long userId);
    
    /**
     * Marcar todas las notificaciones de un usuario como leídas
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :now WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    /**
     * Contar notificaciones por tipo
     */
    @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE n.user.id = :userId GROUP BY n.type")
    List<Object[]> countByUserIdGroupByType(@Param("userId") Long userId);
    
    /**
     * Contar notificaciones por método de entrega
     */
    @Query("SELECT n.deliveryMethod, COUNT(n) FROM Notification n WHERE n.user.id = :userId GROUP BY n.deliveryMethod")
    List<Object[]> countByUserIdGroupByDeliveryMethod(@Param("userId") Long userId);
    
    /**
     * Contar notificaciones de hoy
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND FUNCTION('DATE_TRUNC', 'day', n.createdAt) = FUNCTION('DATE_TRUNC', 'day', CURRENT_TIMESTAMP)")
    long countTodayNotificationsByUserId(@Param("userId") Long userId);
    
    /**
     * Contar notificaciones de esta semana
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :weekStart")
    long countThisWeekNotificationsByUserId(@Param("userId") Long userId, @Param("weekStart") LocalDateTime weekStart);
} 