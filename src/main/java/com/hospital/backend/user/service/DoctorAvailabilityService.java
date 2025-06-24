package com.hospital.backend.user.service;

import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.common.exception.ValidationException;
import com.hospital.backend.enums.TimeBlock;
import com.hospital.backend.user.dto.request.DoctorAvailabilityRequest;
import com.hospital.backend.user.dto.response.DoctorAvailabilityResponse;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.entity.DoctorAvailability;
import com.hospital.backend.user.repository.DoctorAvailabilityRepository;
import com.hospital.backend.user.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de disponibilidad de doctores
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final DoctorRepository doctorRepository;
    
    @Transactional(readOnly = true)
    public List<DoctorAvailabilityResponse> findByDoctorId(Long doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        List<DoctorAvailability> availabilities = doctorAvailabilityRepository.findByDoctorId(doctorId);
        
        // Agrupar por día de la semana
        Map<Integer, List<DoctorAvailability>> availabilitiesByDay = new HashMap<>();
        for (DoctorAvailability availability : availabilities) {
            availabilitiesByDay
                .computeIfAbsent(availability.getDayOfWeek(), k -> new ArrayList<>())
                .add(availability);
        }
        
        return availabilitiesByDay.entrySet().stream()
                .map(entry -> createDayAvailabilityResponse(entry.getKey(), entry.getValue(), doctor))
                .collect(Collectors.toList());
    }
    
    public List<DoctorAvailabilityResponse> saveAvailabilities(Long doctorId, 
                                                               List<DoctorAvailabilityRequest> requests) {
        Doctor doctor = getDoctorById(doctorId);
        
        // Eliminar horarios existentes
        doctorAvailabilityRepository.deleteByDoctorId(doctorId);
        
        List<DoctorAvailability> newAvailabilities = new ArrayList<>();
        Map<Integer, List<DoctorAvailability>> availabilitiesByDay = new HashMap<>();
        
        for (DoctorAvailabilityRequest request : requests) {
            validateAvailabilityRequest(request);
            
            DoctorAvailability availability = new DoctorAvailability();
            availability.setDoctor(doctor);
            availability.setDayOfWeek(request.getDayOfWeek());
            availability.setTimeBlock(request.getTimeBlock());
            availability.setMaxPatients(request.getMaxPatients());
            availability.setIsAvailable(request.getIsAvailable());
            
            newAvailabilities.add(availability);
            availabilitiesByDay
                .computeIfAbsent(request.getDayOfWeek(), k -> new ArrayList<>())
                .add(availability);
        }
        
        // Validar no duplicados de bloques por día
        for (Map.Entry<Integer, List<DoctorAvailability>> entry : availabilitiesByDay.entrySet()) {
            validateNoDuplicateBlocks(entry.getValue(), entry.getKey());
        }
        
        List<DoctorAvailability> savedAvailabilities = doctorAvailabilityRepository.saveAll(newAvailabilities);
        log.info("Guardados {} bloques de disponibilidad para el doctor con ID: {}", savedAvailabilities.size(), doctorId);
        
        // Agrupar por día para la respuesta
        Map<Integer, List<DoctorAvailability>> savedByDay = new HashMap<>();
        for (DoctorAvailability saved : savedAvailabilities) {
            savedByDay
                .computeIfAbsent(saved.getDayOfWeek(), k -> new ArrayList<>())
                .add(saved);
        }
        
        return savedByDay.entrySet().stream()
                .map(entry -> createDayAvailabilityResponse(entry.getKey(), entry.getValue(), doctor))
                .collect(Collectors.toList());
    }
    
    public void deleteAvailability(Long availabilityId) {
        DoctorAvailability availability = doctorAvailabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad", "id", availabilityId));
        
        doctorAvailabilityRepository.delete(availability);
        log.info("Eliminada disponibilidad ID: {}", availabilityId);
    }
    
    @Transactional(readOnly = true)
    public boolean isDoctorAvailableOnDayAndTimeBlock(Long doctorId, int dayOfWeek, TimeBlock timeBlock) {
        Optional<DoctorAvailability> availability = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndTimeBlockAndIsAvailable(doctorId, dayOfWeek, timeBlock, true)
                .stream()
                .findFirst();
        
        return availability.isPresent();
    }
    
    private Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
    }
    
    private void validateAvailabilityRequest(DoctorAvailabilityRequest request) {
        // Validar día de la semana (1-7, donde 1 es lunes)
        if (request.getDayOfWeek() < 1 || request.getDayOfWeek() > 7) {
            throw new ValidationException("El día de la semana debe estar entre 1 (Lunes) y 7 (Domingo)");
        }
        
        // Validar bloque de tiempo
        if (request.getTimeBlock() == null) {
            throw new ValidationException("El bloque de tiempo es obligatorio");
        }
        
        // Validar capacidad máxima
        if (request.getMaxPatients() != null && request.getMaxPatients() <= 0) {
            throw new ValidationException("La capacidad máxima debe ser mayor a cero");
        }
    }
    
    private void validateNoDuplicateBlocks(List<DoctorAvailability> availabilities, int dayOfWeek) {
        Map<TimeBlock, Long> blockCounts = availabilities.stream()
                .collect(Collectors.groupingBy(DoctorAvailability::getTimeBlock, Collectors.counting()));
        
        blockCounts.forEach((block, count) -> {
            if (count > 1) {
                String dayName = DayOfWeek.of(dayOfWeek).name();
                throw new BusinessException("Existe más de una configuración para el bloque " + 
                        block.getDisplayName() + " el día " + dayName);
            }
        });
    }
    
    private DoctorAvailabilityResponse createDayAvailabilityResponse(
            int dayOfWeek, List<DoctorAvailability> availabilities, Doctor doctor) {
            
        DoctorAvailabilityResponse response = new DoctorAvailabilityResponse();
        response.setDoctorId(doctor.getId());
        response.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
        response.setDayOfWeek(dayOfWeek);
        response.setDayName(DayOfWeek.of(dayOfWeek).name());
        
        List<DoctorAvailabilityResponse.BlockDto> blocks = availabilities.stream()
                .map(this::mapToBlockDto)
                .collect(Collectors.toList());
        
        response.setBlocks(blocks);
        return response;
    }
    
    private DoctorAvailabilityResponse.BlockDto mapToBlockDto(DoctorAvailability availability) {
        DoctorAvailabilityResponse.BlockDto blockDto = new DoctorAvailabilityResponse.BlockDto();
        blockDto.setId(availability.getId());
        blockDto.setTimeBlock(availability.getTimeBlock());
        blockDto.setBlockName(availability.getTimeBlock().getDisplayName());
        blockDto.setMaxPatients(availability.getMaxPatients());
        blockDto.setIsAvailable(availability.getIsAvailable());
        
        return blockDto;
    }
}
