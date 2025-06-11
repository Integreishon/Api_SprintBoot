package com.hospital.backend.appointment.service;

import com.hospital.backend.appointment.dto.request.AvailableSlotsRequest;
import com.hospital.backend.appointment.dto.response.AvailableSlotResponse;
import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.catalog.entity.Specialty;
import com.hospital.backend.catalog.repository.SpecialtyRepository;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.entity.DoctorAvailability;
import com.hospital.backend.user.repository.DoctorRepository;
import com.hospital.backend.user.repository.DoctorAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de disponibilidad de citas
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AvailabilityService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final int DEFAULT_SLOT_DURATION = 30; // minutos

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final SpecialtyRepository specialtyRepository;

    /**
     * Verifica si un slot de tiempo está disponible para un doctor
     */
    public boolean isSlotAvailable(Long doctorId, LocalDate date, LocalTime startTime) {
        // Verificar que la fecha y hora no estén en el pasado
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            return false;
        }
        if (date.equals(today) && startTime.isBefore(LocalTime.now())) {
            return false;
        }

        // Buscar doctor
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));
        log.debug("Verificando disponibilidad para doctor: {}", doctor.getFirstName());

        // Verificar que el doctor trabaja ese día de la semana
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Lunes, 7=Domingo
        // Ajustar para que coincida con la BD (1=Lunes, 6=Sábado)
        if (dayOfWeek == 7) dayOfWeek = 0; // Domingo = 0, pero no trabajan domingos
        if (dayOfWeek == 0) return false; // No trabajan domingos

        List<DoctorAvailability> availabilities = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndIsActive(doctorId, dayOfWeek, true);
        
        if (availabilities.isEmpty()) {
            return false;
        }

        // Verificar si la hora está dentro del horario del doctor
        boolean withinSchedule = availabilities.stream()
                .anyMatch(availability -> 
                        !startTime.isBefore(availability.getStartTime()) && 
                        !startTime.isAfter(availability.getEndTime().minusMinutes(30))); // Al menos 30 min antes de terminar

        if (!withinSchedule) {
            return false;
        }

        // Verificar si ya existe una cita para ese horario
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
        
        // Verificar que no haya solapamiento con otras citas
        for (Appointment appointment : appointments) {
            // Estados que bloquean el horario
            List<AppointmentStatus> blockingStatuses = Arrays.asList(
                    AppointmentStatus.SCHEDULED, 
                    AppointmentStatus.CONFIRMED);
            
            if (!blockingStatuses.contains(appointment.getStatus())) {
                continue; // Si la cita está cancelada o completada, no bloquea
            }
            
            // Verificar solapamiento
            LocalTime appointmentStartTime = appointment.getStartTime();
            LocalTime appointmentEndTime = appointment.getEndTime();
            
            // Si la hora de inicio solicitada está dentro del rango de otra cita
            if (!startTime.isBefore(appointmentStartTime) && startTime.isBefore(appointmentEndTime)) {
                return false;
            }
            
            // Calcular hora fin aproximada (30 min después del inicio)
            LocalTime endTime = startTime.plusMinutes(30);
            
            // Si la hora fin solicitada está dentro del rango de otra cita
            if (endTime.isAfter(appointmentStartTime) && !endTime.isAfter(appointmentEndTime)) {
                return false;
            }
            
            // Si la cita solicitada envuelve completamente otra cita
            if (startTime.isBefore(appointmentStartTime) && endTime.isAfter(appointmentEndTime)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Obtener slots de tiempo disponibles para un doctor y especialidad en una fecha
     */
    public AvailableSlotResponse findAvailableSlots(AvailableSlotsRequest request) {
        // Buscar doctor y especialidad
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));
        log.debug("Verificando disponibilidad para doctor: {}", doctor.getFirstName());

        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", request.getSpecialtyId()));

        // Verificar que el doctor pertenece a la especialidad
        boolean doctorHasSpecialty = doctor.getSpecialties().stream()
                .anyMatch(ds -> ds.getSpecialty().getId().equals(specialty.getId()));
        if (!doctorHasSpecialty) {
            throw new BusinessException("El doctor no pertenece a la especialidad seleccionada");
        }

        // Obtener duración de los slots
        Integer slotDuration = request.getSlotDuration() != null ? 
                request.getSlotDuration() : specialty.getAverageDuration();
        
        if (slotDuration == null) {
            slotDuration = DEFAULT_SLOT_DURATION;
        }

        // Obtener horario del doctor para ese día
        int dayOfWeek = request.getDate().getDayOfWeek().getValue(); // 1=Lunes, 7=Domingo
        // Ajustar para que coincida con la BD (1=Lunes, 6=Sábado)
        if (dayOfWeek == 7) dayOfWeek = 0; // Domingo = 0, pero no trabajan domingos
        
        List<DoctorAvailability> availabilities = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndIsActive(request.getDoctorId(), dayOfWeek, true);
        
        if (availabilities.isEmpty()) {
            // El doctor no trabaja ese día
            return buildEmptyResponse(request, doctor, specialty, slotDuration);
        }

        // Obtener citas existentes para ese día
        List<Appointment> existingAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentDate(request.getDoctorId(), request.getDate());
        
        // Filtrar solo citas que bloqueen horarios (programadas o confirmadas)
        List<Appointment> blockingAppointments = existingAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED || 
                              a.getStatus() == AppointmentStatus.CONFIRMED)
                .collect(Collectors.toList());

        // Lista para almacenar slots disponibles
        List<AvailableSlotResponse.TimeSlot> availableSlots = new ArrayList<>();

        // Para cada horario del doctor en ese día
        for (DoctorAvailability availability : availabilities) {
            // Hora de inicio
            LocalTime startTime = availability.getStartTime();
            
            // Hora de fin
            LocalTime endTime = availability.getEndTime();
            
            // Filtrar por horarios específicos si se proporcionaron
            if (request.getStartHour() != null) {
                LocalTime requestedStartTime = LocalTime.parse(request.getStartHour());
                if (requestedStartTime.isAfter(startTime)) {
                    startTime = requestedStartTime;
                }
            }
            
            if (request.getEndHour() != null) {
                LocalTime requestedEndTime = LocalTime.parse(request.getEndHour());
                if (requestedEndTime.isBefore(endTime)) {
                    endTime = requestedEndTime;
                }
            }
            
            // Generar slots
            LocalTime currentSlotStart = startTime;
            
            while (currentSlotStart.plusMinutes(slotDuration).isBefore(endTime) || 
                   currentSlotStart.plusMinutes(slotDuration).equals(endTime)) {
                
                LocalTime currentSlotEnd = currentSlotStart.plusMinutes(slotDuration);
                
                // Verificar si el slot está disponible (no se solapa con citas existentes)
                boolean isAvailable = isTimeSlotAvailable(
                        currentSlotStart, 
                        currentSlotEnd, 
                        blockingAppointments);
                
                // Si es hoy, verificar también que no sea en el pasado
                if (request.getDate().equals(LocalDate.now()) && currentSlotStart.isBefore(LocalTime.now())) {
                    isAvailable = false;
                }
                
                // Añadir a la lista de slots
                availableSlots.add(new AvailableSlotResponse.TimeSlot(
                        currentSlotStart.format(TIME_FORMATTER),
                        currentSlotEnd.format(TIME_FORMATTER),
                        isAvailable
                ));
                
                // Avanzar al siguiente slot
                currentSlotStart = currentSlotEnd;
            }
        }
        
        // Construir respuesta
        AvailableSlotResponse response = new AvailableSlotResponse();
        response.setDoctorId(doctor.getId());
        response.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
        response.setSpecialtyId(specialty.getId());
        response.setSpecialtyName(specialty.getName());
        response.setDate(request.getDate());
        response.setDayOfWeek(request.getDate().getDayOfWeek().toString());
        response.setSlotDuration(slotDuration);
        response.setAvailableSlots(availableSlots);
        
        return response;
    }

    /**
     * Verifica si un slot de tiempo específico está disponible 
     * (no se solapa con citas existentes)
     */
    private boolean isTimeSlotAvailable(
            LocalTime slotStart, 
            LocalTime slotEnd, 
            List<Appointment> existingAppointments) {
        
        for (Appointment appointment : existingAppointments) {
            LocalTime appointmentStart = appointment.getStartTime();
            LocalTime appointmentEnd = appointment.getEndTime();
            
            // Verificar solapamiento
            if ((slotStart.isBefore(appointmentEnd) || slotStart.equals(appointmentEnd)) &&
                (slotEnd.isAfter(appointmentStart) || slotEnd.equals(appointmentStart))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Construye una respuesta vacía cuando el doctor no trabaja en la fecha solicitada
     */
    private AvailableSlotResponse buildEmptyResponse(
            AvailableSlotsRequest request, 
            Doctor doctor, 
            Specialty specialty, 
            Integer slotDuration) {
        
        AvailableSlotResponse response = new AvailableSlotResponse();
        response.setDoctorId(doctor.getId());
        response.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
        response.setSpecialtyId(specialty.getId());
        response.setSpecialtyName(specialty.getName());
        response.setDate(request.getDate());
        response.setDayOfWeek(request.getDate().getDayOfWeek().toString());
        response.setSlotDuration(slotDuration);
        response.setAvailableSlots(new ArrayList<>());
        
        return response;
    }
}