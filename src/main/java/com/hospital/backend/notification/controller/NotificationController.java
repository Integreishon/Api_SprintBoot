package com.hospital.backend.notification.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.notification.dto.request.BulkNotificationRequest;
import com.hospital.backend.notification.dto.request.CreateNotificationRequest;
import com.hospital.backend.notification.dto.request.UpdateNotificationRequest;
import com.hospital.backend.notification.dto.response.NotificationCountResponse;
import com.hospital.backend.notification.dto.response.NotificationResponse;
import com.hospital.backend.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de notificaciones
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Crear una nueva notificación
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        NotificationResponse createdNotification = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notificación creada exitosamente", createdNotification));
    }
    
    /**
     * Crear notificaciones en masa
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> createBulkNotifications(@Valid @RequestBody BulkNotificationRequest request) {
        List<NotificationResponse> createdNotifications = notificationService.createBulkNotifications(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notificaciones creadas exitosamente", createdNotifications));
    }
    
    /**
     * Obtener notificación por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(@PathVariable Long id) {
        NotificationResponse notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(ApiResponse.success("Notificación obtenida exitosamente", notification));
    }
    
    /**
     * Obtener notificaciones por usuario (paginadas)
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getNotificationsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PageResponse<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Notificaciones obtenidas exitosamente", notifications));
    }
    
    /**
     * Obtener notificaciones no leídas por usuario (paginadas)
     */
    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getUnreadNotificationsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        PageResponse<NotificationResponse> notifications = notificationService.getUnreadNotificationsByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Notificaciones no leídas obtenidas exitosamente", notifications));
    }
    
    /**
     * Marcar una notificación como leída
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long id) {
        NotificationResponse notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notificación marcada como leída exitosamente", notification));
    }
    
    /**
     * Marcar todas las notificaciones de un usuario como leídas
     */
    @PutMapping("/user/{userId}/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<Integer>> markAllAsReadByUserId(@PathVariable Long userId) {
        int count = notificationService.markAllAsReadByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(count + " notificaciones marcadas como leídas", count));
    }
    
    /**
     * Actualizar notificación
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<NotificationResponse>> updateNotification(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateNotificationRequest request) {
        
        NotificationResponse updatedNotification = notificationService.updateNotification(id, request);
        return ResponseEntity.ok(ApiResponse.success("Notificación actualizada exitosamente", updatedNotification));
    }
    
    /**
     * Eliminar notificación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Notificación eliminada exitosamente", null));
    }
    
    /**
     * Obtener conteo de notificaciones por usuario
     */
    @GetMapping("/user/{userId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<NotificationCountResponse>> getNotificationCountsByUserId(@PathVariable Long userId) {
        NotificationCountResponse counts = notificationService.getNotificationCountsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Conteo de notificaciones obtenido exitosamente", counts));
    }
} 