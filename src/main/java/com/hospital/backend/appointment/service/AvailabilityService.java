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
     * Obtener bloques disponibles para un doctor en una fecha específica.
     * Versión final y corregida: se basa en los horarios explícitos del doctor para un día,
     * ignorando FULL_DAY y garantizando coherencia con la base de datos.
     */
    @Transactional(readOnly = true)
    public List<BlockAvailabilityResponse> getAvailableBlocks(Long doctorId, LocalDate date) {
        log.info("Calculando disponibilidad estricta (sin FULL_DAY) para doctor: {}, fecha: {}", doctorId, date);

        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayNumber = dayOfWeek.getValue(); // 1=Lunes, 7=Domingo

        // 1. Obtener TODOS los horarios configurados para el doctor en ese día de la semana.
        List<DoctorAvailability> allSchedulesForDay = doctorAvailabilityRepository.findByDoctorIdAndDayOfWeek(doctorId, dayNumber);

        List<BlockAvailabilityResponse> responseBlocks = new ArrayList<>();
        List<TimeBlock> blocksToCheck = List.of(TimeBlock.MORNING, TimeBlock.AFTERNOON);

        for (TimeBlock block : blocksToCheck) {
            // 2. Buscar en la lista obtenida si existe una entrada para el bloque actual (MORNING o AFTERNOON).
            DoctorAvailability schedule = allSchedulesForDay.stream()
                    .filter(s -> s.getTimeBlock() == block && s.getIsAvailable()) // Solo considerar si está disponible.
                    .findFirst()
                    .orElse(null);

            if (schedule != null) {
                // 3. Si se encuentra un horario y está marcado como disponible, verificar los cupos.
                int maxPatients = schedule.getMaxPatients();
                long appointmentsCount = appointmentRepository.countByDoctorIdAndAppointmentDateAndTimeBlock(
                        doctorId, date, block);
                boolean hasCapacity = appointmentsCount < maxPatients;

                responseBlocks.add(BlockAvailabilityResponse.builder()
                        .timeBlock(block)
                        .isAvailable(hasCapacity)
                        .maxPatients(maxPatients)
                        .currentPatients((int) appointmentsCount)
                        .remainingSlots(maxPatients - (int) appointmentsCount)
                        .build());
            } else {
                // 4. Si no se encuentra un horario para este bloque, no está disponible.
                responseBlocks.add(BlockAvailabilityResponse.builder()
                        .timeBlock(block)
                        .isAvailable(false)
                        .maxPatients(0)
                        .currentPatients(0)
                        .remainingSlots(0)
                        .build());
            }
        }

        return responseBlocks;
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


