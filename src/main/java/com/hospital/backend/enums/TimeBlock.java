package com.hospital.backend.enums;

import java.time.LocalTime;

/**
 * Bloques de tiempo para citas médicas
 */
public enum TimeBlock {
    MORNING("Mañana", LocalTime.of(7, 0), LocalTime.of(13, 0), 20),
    AFTERNOON("Tarde", LocalTime.of(16, 0), LocalTime.of(20, 0), 25),
    FULL_DAY("Día completo", LocalTime.of(7, 0), LocalTime.of(20, 0), 45);
    
    private final String displayName;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int defaultCapacity;
    
    TimeBlock(String displayName, LocalTime startTime, LocalTime endTime, int defaultCapacity) {
        this.displayName = displayName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.defaultCapacity = defaultCapacity;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public int getDefaultCapacity() {
        return defaultCapacity;
    }
} 