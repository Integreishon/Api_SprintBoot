package com.hospital.backend.appointment.service;

import com.hospital.backend.appointment.dto.response.BlockAvailabilityResponse;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.catalog.repository.SpecialtyRepository;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.TimeBlock;
import com.hospital.backend.user.entity.DoctorAvailability;
import com.hospital.backend.user.repository.DoctorAvailabilityRepository;
import com.hospital.backend.user.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para calcular disponibilidad de doctores por bloques de tiempo
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
     * Obtener bloques disponibles para un doctor en una fecha específica
     */
    @Transactional(readOnly = true)
    public List<BlockAvailabilityResponse> getAvailableBlocks(Long doctorId, LocalDate date) {
        log.info("Calculando bloques disponibles para doctor: {}, fecha: {}", doctorId, date);
        
        // Validar que el doctor existe
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }
        
        // Obtener la configuración de horarios del doctor para el día de la semana
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayNumber = dayOfWeek.getValue(); // 1=Lunes, 7=Domingo
        
        List<DoctorAvailability> doctorSchedules = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndIsAvailable(doctorId, dayNumber, true);
        
        if (doctorSchedules.isEmpty()) {
            log.info("Doctor {} no trabaja los {}", doctorId, dayOfWeek);
            return new ArrayList<>();
        }
        
        List<BlockAvailabilityResponse> availableBlocks = new ArrayList<>();
        
        // Para cada bloque de trabajo del doctor
        for (DoctorAvailability schedule : doctorSchedules) {
            TimeBlock timeBlock = schedule.getTimeBlock();
            int maxPatients = schedule.getMaxPatients();
            
            // Contar cuántas citas ya hay en este bloque
            long appointmentsCount = appointmentRepository.countByDoctorIdAndAppointmentDateAndTimeBlock(
                    doctorId, date, timeBlock);
            
            boolean hasCapacity = appointmentsCount < maxPatients;
            
            availableBlocks.add(BlockAvailabilityResponse.builder()
                    .timeBlock(timeBlock)
                    .isAvailable(hasCapacity)
                    .maxPatients(maxPatients)
                    .currentPatients((int) appointmentsCount)
                    .remainingSlots(maxPatients - (int) appointmentsCount)
                    .build());
        }
        
        return availableBlocks;
    }
    
    /**
     * Verificar si un doctor está disponible en un bloque específico
     */
    @Transactional(readOnly = true)
    public boolean isDoctorAvailableInTimeBlock(Long doctorId, LocalDate date, TimeBlock timeBlock) {
        log.debug("Verificando disponibilidad de doctor: {} en fecha: {} bloque: {}", 
                doctorId, date, timeBlock);
        
        // 1. Verificar que el doctor trabaja ese día en ese bloque
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayNumber = dayOfWeek.getValue();
        
        List<DoctorAvailability> schedules = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndTimeBlockAndIsAvailable(doctorId, dayNumber, timeBlock, true);
        
        if (schedules.isEmpty()) {
            log.debug("Doctor {} no trabaja los {} en bloque {}", doctorId, dayOfWeek, timeBlock);
            return false;
        }
        
        return true;
    }
    
    /**
     * Verificar si un bloque tiene capacidad disponible
     */
    @Transactional(readOnly = true)
    public boolean hasBlockCapacity(Long doctorId, LocalDate date, TimeBlock timeBlock) {
        // Obtener la configuración del bloque
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayNumber = dayOfWeek.getValue();
        
        DoctorAvailability availability = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndTimeBlockAndIsAvailable(doctorId, dayNumber, timeBlock, true)
                .stream()
                .findFirst()
                .orElse(null);
        
        if (availability == null) {
            return false;
        }
        
        int maxPatients = availability.getMaxPatients();
        
        // Contar cuántas citas ya hay en este bloque
        long appointmentsCount = appointmentRepository.countByDoctorIdAndAppointmentDateAndTimeBlock(
                doctorId, date, timeBlock);
        
        return appointmentsCount < maxPatients;
    }
    
    /**
     * Obtener resumen de disponibilidad por especialidad
     */
    @Transactional(readOnly = true)
    public Map<TimeBlock, List<BlockAvailabilityResponse>> getAvailabilityBySpecialty(
            Long specialtyId, LocalDate date) {
        
        // Validar que la especialidad existe
        if (!specialtyRepository.existsById(specialtyId)) {
            throw new ResourceNotFoundException("Specialty", "id", specialtyId);
        }
        
        // Obtener todos los doctores de esa especialidad
        List<Long> doctorIds = doctorRepository.findIdsBySpecialtyId(specialtyId);
        
        List<BlockAvailabilityResponse> allBlocks = new ArrayList<>();
        
        // Para cada doctor, obtener sus bloques disponibles
        for (Long doctorId : doctorIds) {
            List<BlockAvailabilityResponse> doctorBlocks = getAvailableBlocks(doctorId, date);
            
            // Agregar el ID del doctor a cada bloque
            doctorBlocks.forEach(block -> block.setDoctorId(doctorId));
            
            allBlocks.addAll(doctorBlocks);
        }
        
        // Agrupar por bloque de tiempo
        return allBlocks.stream()
                .filter(BlockAvailabilityResponse::getIsAvailable)
                .collect(Collectors.groupingBy(BlockAvailabilityResponse::getTimeBlock));
    }
}


