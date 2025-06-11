package com.hospital.backend.analytics.repository;

import com.hospital.backend.analytics.entity.AnalyticsCache;
import com.hospital.backend.enums.PeriodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con el caché de métricas
 */
@Repository
public interface AnalyticsCacheRepository extends JpaRepository<AnalyticsCache, Long> {

    // Buscar por clave de métrica
    Optional<AnalyticsCache> findByMetricKey(String metricKey);
    
    // Buscar por clave de métrica y rango de fechas
    Optional<AnalyticsCache> findByMetricKeyAndStartDateAndEndDate(
            String metricKey, LocalDate startDate, LocalDate endDate);
    
    // Buscar por clave de métrica, rango de fechas y tipo de período
    Optional<AnalyticsCache> findByMetricKeyAndStartDateAndEndDateAndPeriodType(
            String metricKey, LocalDate startDate, LocalDate endDate, PeriodType periodType);
    
    // Buscar por clave de métrica, rango de fechas, tipo de período y filtros
    Optional<AnalyticsCache> findByMetricKeyAndStartDateAndEndDateAndPeriodTypeAndFilterCriteria(
            String metricKey, LocalDate startDate, LocalDate endDate, PeriodType periodType, String filterCriteria);
    
    // Buscar todos los registros que hayan expirado
    List<AnalyticsCache> findByIsExpiredTrue();
    
    // Limpiar caché antiguo
    @Modifying
    @Transactional
    @Query("DELETE FROM AnalyticsCache ac WHERE ac.isExpired = true OR (ac.ttlSeconds IS NOT NULL AND ac.createdAt < :expiryDate)")
    int cleanupExpiredCache(@Param("expiryDate") java.time.LocalDateTime expiryDate);
    
    // Marcar como expirado por clave de métrica
    @Modifying
    @Transactional
    @Query("UPDATE AnalyticsCache ac SET ac.isExpired = true WHERE ac.metricKey = :metricKey")
    int expireByMetricKey(@Param("metricKey") String metricKey);
    
    // Marcar como expirado por entidad
    @Modifying
    @Transactional
    @Query("UPDATE AnalyticsCache ac SET ac.isExpired = true WHERE ac.metricKey LIKE CONCAT('%', :entityName, '%')")
    int expireByEntityName(@Param("entityName") String entityName);
    
    // Buscar por fuente de datos
    List<AnalyticsCache> findByDataSource(String dataSource);
    
    // Buscar por tipo de período
    List<AnalyticsCache> findByPeriodType(PeriodType periodType);
} 