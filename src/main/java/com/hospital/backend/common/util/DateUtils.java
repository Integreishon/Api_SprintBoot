// Utilidades para manejo de fechas y horas del sistema
package com.hospital.backend.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    
    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static LocalDateTime nowInLima() {
        return LocalDateTime.now(LIMA_ZONE);
    }
    
    public static LocalDate todayInLima() {
        return LocalDate.now(LIMA_ZONE);
    }
    
    public static boolean isWeekday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SUNDAY;
    }
    
    public static boolean isBusinessHour(LocalTime time) {
        return time.isAfter(LocalTime.of(8, 0)) && time.isBefore(LocalTime.of(18, 0));
    }
    
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }
    
    public static boolean isFutureDate(LocalDate date) {
        return date.isAfter(todayInLima());
    }
    
    public static boolean isFutureDateTime(LocalDateTime dateTime) {
        return dateTime.isAfter(nowInLima());
    }
    
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
    
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER);
    }
}