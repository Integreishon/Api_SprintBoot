package com.hospital.backend.appointment.service;

import com.hospital.backend.appointment.dto.request.AvailableSlotsRequest;
import com.hospital.backend.appointment.dto.response.AvailableSlotResponse;
import com.hospital.backend.user.entity.DoctorAvailability;
import com.hospital.backend.user.repository.DoctorAvailabilityRepository;
import com.hospital.backend.user.repository.DoctorRepository;
import com.hospital.backend.catalog.repository.SpecialtyRepository;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para calcular disponibilidad de doctores y horarios libres
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityService {
    
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final SpecialtyRepository specialtyRepository;
    private final AppointmentRepository appointmentRepository;
    
    /**
     * Obtener slots disponibles para un doctor en una fecha específica
     */
    @Transactional(readOnly = true)
    public List<AvailableSlotResponse> getAvailableSlots(AvailableSlotsRequest request) {
        log.info("Calculando slots disponibles para doctor: {}, fecha: {}, especialidad: {}", 
                request.getDoctorId(), request.getDate(), request.getSpecialtyId());
        
        // Validar que el doctor existe
        if (!doctorRepository.existsById(request.getDoctorId())) {
            throw new ResourceNotFoundException("Doctor", "id", request.getDoctorId());
        }
        
        // Validar que la especialidad existe
        if (!specialtyRepository.existsById(request.getSpecialtyId())) {
            throw new ResourceNotFoundException("Specialty", "id", request.getSpecialtyId());
        }
        
        // Obtener la configuración de horarios del doctor para el día de la semana
        DayOfWeek dayOfWeek = request.getDate().getDayOfWeek();
        int dayNumber = dayOfWeek.getValue(); // 1=Lunes, 7=Domingo
        
        List<DoctorAvailability> doctorSchedules = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndIsActive(request.getDoctorId(), dayNumber, true);
        
        if (doctorSchedules.isEmpty()) {
            log.info("Doctor {} no trabaja los {}", request.getDoctorId(), dayOfWeek);
            return new ArrayList<>();
        }
        
        // Usar duración fija de 30 minutos
        Integer appointmentDuration = 30;
        
        List<AvailableSlotResponse> availableSlots = new ArrayList<>();
        
        // Para cada horario de trabajo del doctor
        for (DoctorAvailability schedule : doctorSchedules) {
            List<AvailableSlotResponse> slotsForSchedule = generateSlotsForSchedule(
                    request.getDoctorId(),
                    request.getDate(),
                    schedule,
                    appointmentDuration
            );
            availableSlots.addAll(slotsForSchedule);
        }
        
        // Ordenar por hora
        return availableSlots.stream()
                .sorted((slot1, slot2) -> slot1.getStartTime().compareTo(slot2.getStartTime()))
                .collect(Collectors.toList());
    }
    
    /**
     * Verificar si un doctor está disponible en una fecha y hora específica
     */
    @Transactional(readOnly = true)
    public boolean isDoctorAvailable(Long doctorId, LocalDate date, LocalTime startTime, Integer duration) {
        log.debug("Verificando disponibilidad de doctor: {} en fecha: {} hora: {}", 
                doctorId, date, startTime);
        
        // 1. Verificar que el doctor trabaja ese día
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayNumber = dayOfWeek.getValue();
        
        List<DoctorAvailability> schedules = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndIsActive(doctorId, dayNumber, true);
        
        if (schedules.isEmpty()) {
            log.debug("Doctor {} no trabaja los {}", doctorId, dayOfWeek);
            return false;
        }
        
        // 2. Verificar que la hora está dentro de algún horario de trabajo
        LocalTime endTime = startTime.plusMinutes(duration);
        boolean withinWorkingHours = schedules.stream()
                .anyMatch(schedule -> 
                    !startTime.isBefore(schedule.getStartTime()) && 
                    !endTime.isAfter(schedule.getEndTime())
                );
        
        if (!withinWorkingHours) {
            log.debug("Hora {} - {} no está dentro del horario de trabajo", startTime, endTime);
            return false;
        }
        
        // 3. Verificar que no hay citas conflictivas
        boolean hasConflicts = appointmentRepository.existsByDoctorAndDateAndTime(
                doctorId, date, startTime);
        
        if (hasConflicts) {
            log.debug("Doctor {} ya tiene una cita en {} a las {}", doctorId, date, startTime);
            return false;
        }
        
        return true;
    }
    
    /**
     * Generar slots disponibles para un horario específico del doctor
     */
    private List<AvailableSlotResponse> generateSlotsForSchedule(
            Long doctorId, 
            LocalDate date, 
            DoctorAvailability schedule, 
            Integer appointmentDuration) {
        
        List<AvailableSlotResponse> slots = new ArrayList<>();
        
        LocalTime currentTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();
        Integer slotDuration = schedule.getSlotDuration(); // Duración de cada slot (ej: 30 min)
        
        // Generar slots cada 'slotDuration' minutos
        while (!currentTime.plusMinutes(appointmentDuration).isAfter(endTime)) {
            
            // Verificar si este slot está disponible (no hay cita)
            boolean isAvailable = !appointmentRepository.existsByDoctorAndDateAndTime(
                    doctorId, date, currentTime);
            
            if (isAvailable) {
                LocalTime slotEndTime = currentTime.plusMinutes(appointmentDuration);
                slots.add(new AvailableSlotResponse(
                        currentTime,
                        slotEndTime,
                        appointmentDuration,
                        true));
            }
            
            currentTime = currentTime.plusMinutes(slotDuration);
        }
        
        return slots;
    }
}


