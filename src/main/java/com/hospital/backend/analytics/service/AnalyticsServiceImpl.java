package com.hospital.backend.analytics.service;

import com.hospital.backend.analytics.dto.AnalyticsRequest;
import com.hospital.backend.analytics.dto.AnalyticsResponse;
import com.hospital.backend.analytics.repository.AnalyticsCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsCacheRepository analyticsCacheRepository;

    @Override
    public AnalyticsResponse generateAnalytics(AnalyticsRequest request) {
        // Por ahora, implementación básica que retorna una respuesta vacía
        AnalyticsResponse response = new AnalyticsResponse(
            request.getStartDate(),
            request.getEndDate(),
            request.getPeriodType()
        );
        
        // TODO: Implementar lógica real de generación de métricas
        
        return response;
    }

    @Override
    public int cleanupExpiredCache() {
        LocalDateTime expiryDate = LocalDateTime.now();
        return analyticsCacheRepository.cleanupExpiredCache(expiryDate);
    }

    @Override
    public int invalidateCacheForEntity(String entityName) {
        return analyticsCacheRepository.expireByEntityName(entityName);
    }
} 