package com.hospital.backend.user.service;

import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.common.exception.ValidationException;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de disponibilidad de doctores
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final DoctorRepository doctorRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final int MIN_SLOT_DURATION = 10; // Duración mínima en minutos
    
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
            availability.setStartTime(request.getStartTime());
            availability.setEndTime(request.getEndTime());
            availability.setSlotDuration(request.getSlotDuration());
            
            newAvailabilities.add(availability);
            availabilitiesByDay
                .computeIfAbsent(request.getDayOfWeek(), k -> new ArrayList<>())
                .add(availability);
        }
        
        // Validar no solapamiento de horarios por día
        for (Map.Entry<Integer, List<DoctorAvailability>> entry : availabilitiesByDay.entrySet()) {
            validateNoTimeOverlap(entry.getValue(), entry.getKey());
        }
        
        List<DoctorAvailability> savedAvailabilities = doctorAvailabilityRepository.saveAll(newAvailabilities);
        log.info("Guardados {} horarios para el doctor con ID: {}", savedAvailabilities.size(), doctorId);
        
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
    public boolean isDoctorAvailableOnDayAndTime(Long doctorId, int dayOfWeek, LocalTime time) {
        Optional<DoctorAvailability> availability = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .stream()
                .findFirst();
        
        return availability.map(avail -> 
            !time.isBefore(avail.getStartTime()) && !time.isAfter(avail.getEndTime())
        ).orElse(false);
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
        
        // Validar horario
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new ValidationException("Las horas de inicio y fin son obligatorias");
        }
        
        // Validar que hora inicio < hora fin
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().equals(request.getEndTime())) {
            throw new ValidationException("La hora de inicio debe ser anterior a la hora de fin");
        }
        
        // Validar duración mínima del slot
        if (request.getSlotDuration() < MIN_SLOT_DURATION) {
            throw new ValidationException("La duración mínima de un turno debe ser " + MIN_SLOT_DURATION + " minutos");
        }
    }
    
    private void validateNoTimeOverlap(List<DoctorAvailability> availabilities, int dayOfWeek) {
        if (availabilities.size() <= 1) {
            return;
        }
        
        // Ordenar por hora de inicio
        List<DoctorAvailability> sorted = availabilities.stream()
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .collect(Collectors.toList());
        
        // Verificar solapamientos
        for (int i = 0; i < sorted.size() - 1; i++) {
            DoctorAvailability current = sorted.get(i);
            DoctorAvailability next = sorted.get(i + 1);
            
            if (!current.getEndTime().isBefore(next.getStartTime())) {
                String dayName = DayOfWeek.of(dayOfWeek).name();
                throw new BusinessException("Existen horarios solapados el día " + dayName + 
                        " entre " + current.getStartTime().format(TIME_FORMATTER) + 
                        "-" + current.getEndTime().format(TIME_FORMATTER) + 
                        " y " + next.getStartTime().format(TIME_FORMATTER) + 
                        "-" + next.getEndTime().format(TIME_FORMATTER));
            }
        }
    }
    
    private DoctorAvailabilityResponse createDayAvailabilityResponse(
            int dayOfWeek, List<DoctorAvailability> availabilities, Doctor doctor) {
            
        DoctorAvailabilityResponse response = new DoctorAvailabilityResponse();
        response.setDoctorId(doctor.getId());
        response.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
        response.setDayOfWeek(dayOfWeek);
        response.setDayName(DayOfWeek.of(dayOfWeek).name());
        
        List<DoctorAvailabilityResponse.TimeSlotDto> slots = availabilities.stream()
                .map(this::mapToTimeSlot)
                .collect(Collectors.toList());
        
        response.setTimeSlots(slots);
        return response;
    }
    
    private DoctorAvailabilityResponse.TimeSlotDto mapToTimeSlot(DoctorAvailability availability) {
        DoctorAvailabilityResponse.TimeSlotDto slot = new DoctorAvailabilityResponse.TimeSlotDto();
        slot.setId(availability.getId());
        slot.setStartTime(availability.getStartTime());
        slot.setEndTime(availability.getEndTime());
        slot.setSlotDuration(availability.getSlotDuration());
        
        // Formatear las horas para mostrar
        slot.setStartTimeFormatted(availability.getStartTime().format(TIME_FORMATTER));
        slot.setEndTimeFormatted(availability.getEndTime().format(TIME_FORMATTER));
        
        return slot;
    }
}
