package com.hospital.backend.analytics.service;

import com.hospital.backend.analytics.dto.AnalyticsRequest;
import com.hospital.backend.analytics.dto.AnalyticsResponse;

public interface AnalyticsService {
    AnalyticsResponse generateAnalytics(AnalyticsRequest request);
} 