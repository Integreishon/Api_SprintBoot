package com.hospital.backend.admin.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospital.backend.admin.dto.DashboardResponse;
import com.hospital.backend.admin.dto.DashboardResponse.DoctorStats;
import com.hospital.backend.admin.dto.DashboardResponse.SpecialtyStats;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.payment.repository.PaymentRepository;
import com.hospital.backend.user.repository.DoctorAvailabilityRepository;
import com.hospital.backend.user.repository.DoctorRepository;
import com.hospital.backend.user.repository.PatientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para calcular métricas y estadísticas para el dashboard administrativo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PaymentRepository paymentRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    /**
     * Genera las métricas completas para el dashboard administrativo
     * @return Respuesta con todas las métricas del hospital
     */
    public DashboardResponse getDashboardMetrics() {
        log.info("Generando métricas para el dashboard administrativo");
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusSeconds(1);
        
        // Métricas generales
        Long totalPatients = patientRepository.count();
        Long totalDoctors = doctorRepository.count();
        Long totalAppointments = appointmentRepository.count();
        Long pendingAppointments = appointmentRepository.countByStatus(AppointmentStatus.SCHEDULED);
        BigDecimal totalRevenue = paymentRepository.calculateTotalRevenue();
        
        // Métricas de hoy
        Long todayAppointments = appointmentRepository.countByAppointmentDateBetween(startOfDay, endOfDay);
        Long todayPatients = appointmentRepository.countDistinctPatientByAppointmentDateBetween(startOfDay, endOfDay);
        BigDecimal todayRevenue = paymentRepository.calculateRevenueForDateRange(startOfDay, endOfDay);
        
        // Métricas semanales
        Map<String, Long> appointmentsPerDay = getAppointmentsPerDayForLastWeek();
        Map<String, BigDecimal> revenuePerDay = getRevenuePerDayForLastWeek();
        
        // Top especialidades
        List<SpecialtyStats> topSpecialties = getTopSpecialties(5);
        
        // Top doctores
        List<DoctorStats> topDoctors = getTopDoctors(5);
        
        // Estadísticas de ocupación
        Integer dayOfWeek = today.getDayOfWeek().getValue();
        Long availableSlotsToday = availabilityRepository.countAvailableSlotsForDay(dayOfWeek);
        BigDecimal occupancyRate = calculateOccupancyRate(today);
        
        // Construir respuesta completa
        return DashboardResponse.builder()
                .totalPatientsCount(totalPatients)
                .totalDoctorsCount(totalDoctors)
                .totalAppointmentsCount(totalAppointments)
                .pendingAppointmentsCount(pendingAppointments)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .todayAppointmentsCount(todayAppointments)
                .todayPatientsCount(todayPatients)
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .appointmentsPerDay(appointmentsPerDay)
                .revenuePerDay(revenuePerDay)
                .topSpecialties(topSpecialties)
                .topDoctors(topDoctors)
                .availableSlotsToday(availableSlotsToday)
                .appointmentOccupancyRate(occupancyRate)
                .build();
    }
    
    /**
     * Obtiene el número de citas por día para la última semana
     * @return Mapa con formato "dd/MM" -> conteo
     */
    private Map<String, Long> getAppointmentsPerDayForLastWeek() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        Map<String, Long> result = new HashMap<>();
        
        // Inicializar mapa con últimos 7 días
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            result.put(date.format(formatter), 0L);
        }
        
        // Obtener datos reales
        List<Object[]> appointmentsPerDay = appointmentRepository.countAppointmentsGroupByDayForLastWeek();
        
        // Llenar mapa con datos reales
        for (Object[] data : appointmentsPerDay) {
            LocalDate date = ((java.sql.Date) data[0]).toLocalDate();
            Long count = ((Number) data[1]).longValue();
            result.put(date.format(formatter), count);
        }
        
        return result;
    }
    
    /**
     * Obtiene los ingresos por día para la última semana
     * @return Mapa con formato "dd/MM" -> ingresos
     */
    private Map<String, BigDecimal> getRevenuePerDayForLastWeek() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        Map<String, BigDecimal> result = new HashMap<>();
        
        // Inicializar mapa con últimos 7 días
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            result.put(date.format(formatter), BigDecimal.ZERO);
        }
        
        // Obtener datos reales
        List<Object[]> revenuePerDay = paymentRepository.calculateRevenuePerDayForLastWeek();
        
        // Llenar mapa con datos reales
        for (Object[] data : revenuePerDay) {
            LocalDate date = ((java.sql.Date) data[0]).toLocalDate();
            BigDecimal amount = (BigDecimal) data[1];
            result.put(date.format(formatter), amount);
        }
        
        return result;
    }
    
    /**
     * Obtiene las especialidades con más citas e ingresos
     * @param limit Número máximo de especialidades a retornar
     * @return Lista de estadísticas de especialidades
     */
    private List<SpecialtyStats> getTopSpecialties(int limit) {
        List<Object[]> topSpecialtiesData = appointmentRepository.findTopSpecialties(limit);
        
        return topSpecialtiesData.stream()
            .map(data -> {
                Long specialtyId = ((Number) data[0]).longValue();
                String specialtyName = (String) data[1];
                Long appointmentsCount = ((Number) data[2]).longValue();
                BigDecimal revenue = (BigDecimal) data[3];
                
                return SpecialtyStats.builder()
                        .specialtyId(specialtyId)
                        .specialtyName(specialtyName)
                        .appointmentsCount(appointmentsCount)
                        .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                        .build();
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los doctores con más citas e ingresos
     * @param limit Número máximo de doctores a retornar
     * @return Lista de estadísticas de doctores
     */
    private List<DoctorStats> getTopDoctors(int limit) {
        List<Object[]> topDoctorsData = appointmentRepository.findTopDoctors(limit);
        
        return topDoctorsData.stream()
            .map(data -> {
                Long doctorId = ((Number) data[0]).longValue();
                String doctorName = (String) data[1];
                String specialtyName = (String) data[2];
                Long appointmentsCount = ((Number) data[3]).longValue();
                BigDecimal revenue = (BigDecimal) data[4];
                Double rating = data[5] != null ? ((Number) data[5]).doubleValue() : null;
                
                return DoctorStats.builder()
                        .doctorId(doctorId)
                        .doctorName(doctorName)
                        .specialtyName(specialtyName)
                        .appointmentsCount(appointmentsCount)
                        .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                        .rating(rating)
                        .build();
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Calcula la tasa de ocupación de citas para un día específico
     * @param date Fecha para calcular la ocupación
     * @return Porcentaje de ocupación
     */
    private BigDecimal calculateOccupancyRate(LocalDate date) {
        Integer dayOfWeek = date.getDayOfWeek().getValue();
        Long totalSlots = availabilityRepository.countTotalSlotsForDay(dayOfWeek);
        Long scheduledAppointments = appointmentRepository.countByAppointmentDateBetween(
                date.atStartOfDay(), 
                date.plusDays(1).atStartOfDay().minusSeconds(1)
        );
        
        if (totalSlots == null || totalSlots == 0) {
            return BigDecimal.ZERO;
        }
        
        return new BigDecimal(scheduledAppointments)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(totalSlots), 2, RoundingMode.HALF_UP);
    }
} 