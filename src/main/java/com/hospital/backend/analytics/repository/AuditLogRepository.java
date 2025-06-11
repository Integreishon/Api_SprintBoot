package com.hospital.backend.analytics.repository;

import com.hospital.backend.analytics.entity.AuditLog;
import com.hospital.backend.enums.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones con logs de auditoría
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Buscar logs por usuario
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);
    
    // Buscar logs por tipo de operación
    Page<AuditLog> findByOperationType(OperationType operationType, Pageable pageable);
    
    // Buscar logs por entidad
    Page<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId, Pageable pageable);
    
    // Buscar logs por rango de fechas
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    // Buscar logs por tipo de operación y rango de fechas
    @Query("SELECT a FROM AuditLog a WHERE a.operationType = :operationType AND a.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByOperationTypeAndDateRange(
            @Param("operationType") OperationType operationType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    // Buscar logs por usuario y rango de fechas
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    // Buscar logs por módulo
    Page<AuditLog> findByModule(String module, Pageable pageable);
    
    // Contar logs por tipo de operación y rango de fechas
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.operationType = :operationType AND a.createdAt BETWEEN :startDate AND :endDate")
    Long countByOperationTypeAndDateRange(
            @Param("operationType") OperationType operationType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Buscar los usuarios más activos en un período
    @Query("SELECT a.userId, a.username, COUNT(a) as loginCount FROM AuditLog a WHERE a.operationType = 'LOGIN' AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY a.userId, a.username ORDER BY loginCount DESC")
    List<Object[]> findMostActiveUsers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    // Contar errores por módulo en un período
    @Query("SELECT a.module, COUNT(a) as errorCount FROM AuditLog a WHERE a.successful = false AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY a.module ORDER BY errorCount DESC")
    List<Object[]> countErrorsByModule(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Buscar por éxito/error
    Page<AuditLog> findBySuccessful(Boolean successful, Pageable pageable);
    
    // Búsqueda avanzada
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:operationType IS NULL OR a.operationType = :operationType) AND " +
           "(:entityName IS NULL OR a.entityName = :entityName) AND " +
           "(:entityId IS NULL OR a.entityId = :entityId) AND " +
           "(:module IS NULL OR a.module = :module) AND " +
           "(:successful IS NULL OR a.successful = :successful) AND " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate)")
    Page<AuditLog> search(
            @Param("userId") Long userId,
            @Param("operationType") OperationType operationType,
            @Param("entityName") String entityName,
            @Param("entityId") Long entityId,
            @Param("module") String module,
            @Param("successful") Boolean successful,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
} 