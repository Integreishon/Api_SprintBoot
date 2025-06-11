package com.hospital.backend.appointment.service;

import com.hospital.backend.appointment.dto.request.CreateAppointmentRequest;
import com.hospital.backend.appointment.dto.request.UpdateAppointmentRequest;
import com.hospital.backend.appointment.dto.response.AppointmentResponse;
import com.hospital.backend.appointment.dto.response.AppointmentSummaryResponse;
import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.catalog.entity.Specialty;
import com.hospital.backend.catalog.repository.SpecialtyRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.common.exception.ValidationException;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.entity.Patient;
import com.hospital.backend.user.repository.DoctorRepository;
import com.hospital.backend.user.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de citas médicas
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final AvailabilityService availabilityService;

    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        Appointment appointment = getAppointmentById(id);
        return mapToAppointmentResponse(appointment);
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> findAll(Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findAll(pageable);
        Page<AppointmentResponse> appointmentResponses = appointments.map(this::mapToAppointmentResponse);
        return new PageResponse<>(appointmentResponses);
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> findByPatient(Long patientId, Pageable pageable) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", patientId));

        Page<Appointment> appointments = appointmentRepository.findByPatientId(patientId, pageable);
        Page<AppointmentResponse> appointmentResponses = appointments.map(this::mapToAppointmentResponse);
        return new PageResponse<>(appointmentResponses);
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> findByDoctor(Long doctorId, Pageable pageable) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        Page<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId, pageable);
        Page<AppointmentResponse> appointmentResponses = appointments.map(this::mapToAppointmentResponse);
        return new PageResponse<>(appointmentResponses);
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> findByDate(LocalDate date, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByAppointmentDate(date, pageable);
        Page<AppointmentResponse> appointmentResponses = appointments.map(this::mapToAppointmentResponse);
        return new PageResponse<>(appointmentResponses);
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> findByStatus(AppointmentStatus status, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByStatus(status, pageable);
        Page<AppointmentResponse> appointmentResponses = appointments.map(this::mapToAppointmentResponse);
        return new PageResponse<>(appointmentResponses);
    }

    public AppointmentResponse create(CreateAppointmentRequest request) {
        // Validar datos de entrada
        validateCreateAppointmentRequest(request);

        // Obtener entidades relacionadas
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", request.getSpecialtyId()));

        // Verificar que la fecha y hora estén disponibles
        if (!availabilityService.isSlotAvailable(
                doctor.getId(),
                request.getAppointmentDate(),
                request.getStartTime())) {
            throw new BusinessException("El horario seleccionado no está disponible para el doctor");
        }

        // Crear la cita
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSpecialty(specialty);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());

        // Calcular hora de finalización basada en la duración de la especialidad
        LocalTime endTime = request.getStartTime().plusMinutes(specialty.getAverageDuration());
        appointment.setEndTime(endTime);

        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        appointment.setInPerson(request.getInPerson());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPaymentStatus(PaymentStatus.PENDING);

        // Calcular precio basado en la especialidad
        appointment.setPrice(specialty.getFinalPrice());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Cita creada con ID: {} para el paciente: {} con el doctor: {} en la fecha: {}",
                savedAppointment.getId(), patient.getId(), doctor.getId(), request.getAppointmentDate());

        return mapToAppointmentResponse(savedAppointment);
    }

    public AppointmentResponse update(Long id, UpdateAppointmentRequest request) {
        Appointment appointment = getAppointmentById(id);

        // Validar que la cita pueda ser actualizada según su estado actual
        validateAppointmentStatus(appointment, request.getStatus());

        // Actualizar campos
        if (request.getAppointmentDate() != null && request.getStartTime() != null) {
            // Verificar disponibilidad si la fecha/hora ha cambiado
            if (!appointment.getAppointmentDate().equals(request.getAppointmentDate()) ||
                    !appointment.getStartTime().equals(request.getStartTime())) {
                
                if (!availabilityService.isSlotAvailable(
                        appointment.getDoctor().getId(),
                        request.getAppointmentDate(),
                        request.getStartTime())) {
                    throw new BusinessException("El nuevo horario seleccionado no está disponible");
                }
                
                appointment.setAppointmentDate(request.getAppointmentDate());
                appointment.setStartTime(request.getStartTime());
                
                // Recalcular hora de finalización
                LocalTime endTime = request.getStartTime().plusMinutes(
                        appointment.getSpecialty().getAverageDuration());
                appointment.setEndTime(endTime);
            }
        }

        if (request.getReason() != null) {
            appointment.setReason(request.getReason());
        }

        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }

        if (request.getInPerson() != null) {
            appointment.setInPerson(request.getInPerson());
        }

        if (request.getVirtualMeetingUrl() != null) {
            appointment.setVirtualMeetingUrl(request.getVirtualMeetingUrl());
        }

        // Actualizar estado
        if (request.getStatus() != null && request.getStatus() != appointment.getStatus()) {
            appointment.setStatus(request.getStatus());
            
            // Si se está cancelando, registrar motivo
            if (request.getStatus() == AppointmentStatus.CANCELLED && request.getCancellationReason() != null) {
                appointment.setCancellationReason(request.getCancellationReason());
            }
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Cita actualizada con ID: {}", updatedAppointment.getId());

        return mapToAppointmentResponse(updatedAppointment);
    }

    public void delete(Long id) {
        Appointment appointment = getAppointmentById(id);
        
        // Validar que la cita pueda ser eliminada
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("No se puede eliminar una cita ya completada");
        }
        
        appointmentRepository.delete(appointment);
        log.info("Cita eliminada con ID: {}", id);
    }
    
    public AppointmentResponse cancel(Long id, String reason) {
        Appointment appointment = getAppointmentById(id);
        
        // Validar que la cita pueda ser cancelada
        if (!appointment.canCancel()) {
            throw new BusinessException("La cita no se puede cancelar en su estado actual: " + 
                    appointment.getStatus().getDisplayName());
        }
        
        // Actualizar estado
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Cita cancelada con ID: {}", id);
        
        return mapToAppointmentResponse(updatedAppointment);
    }
    
    public AppointmentResponse complete(Long id, String notes) {
        Appointment appointment = getAppointmentById(id);
        
        // Validar que la cita pueda ser marcada como completada
        if (!appointment.canComplete()) {
            throw new BusinessException("La cita no puede ser completada en su estado actual: " + 
                    appointment.getStatus().getDisplayName());
        }
        
        // Actualizar estado y notas
        appointment.setStatus(AppointmentStatus.COMPLETED);
        if (notes != null) {
            appointment.setNotes(notes);
        }
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Cita completada con ID: {}", id);
        
        return mapToAppointmentResponse(updatedAppointment);
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findUpcomingAppointmentsForPatient(Long patientId) {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekLater = today.plusDays(7);
        
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndDateRange(
                patientId, today, oneWeekLater);
        
        return appointments.stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findTodayAppointmentsForDoctor(Long doctorId) {
        LocalDate today = LocalDate.now();
        List<AppointmentStatus> activeStatuses = List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);
        
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndDateAndStatusIn(
                doctorId, today, activeStatuses);
        
        return appointments.stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public AppointmentSummaryResponse generateSummary(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetween(startDate, endDate);
        
        // Total de citas
        long totalAppointments = appointments.size();
        
        if (totalAppointments == 0) {
            return new AppointmentSummaryResponse();
        }
        
        // Citas por estado
        Map<AppointmentStatus, Long> countsByStatus = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
        
        // Citas por especialidad
        Map<String, Long> countsBySpecialty = appointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getSpecialty().getName(),
                        Collectors.counting()));
        
        // Tasa de cancelaciones por doctor
        Map<Long, List<Appointment>> appointmentsByDoctor = appointments.stream()
                .collect(Collectors.groupingBy(a -> a.getDoctor().getId()));
        
        Map<String, Double> cancellationRateByDoctor = new HashMap<>();
        
        appointmentsByDoctor.forEach((doctorId, doctorAppointments) -> {
            long totalForDoctor = doctorAppointments.size();
            long cancelledForDoctor = doctorAppointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                    .count();
            
            String doctorName = doctorAppointments.get(0).getDoctor().getFirstName() + " " +
                    doctorAppointments.get(0).getDoctor().getLastName();
            
            double rate = (double) cancelledForDoctor / totalForDoctor;
            cancellationRateByDoctor.put(doctorName, rate);
        });
        
        // Tasas globales
        long noShowCount = countsByStatus.getOrDefault(AppointmentStatus.NO_SHOW, 0L);
        double noShowRate = (double) noShowCount / totalAppointments;
        
        long confirmedCount = countsByStatus.getOrDefault(AppointmentStatus.CONFIRMED, 0L);
        double confirmationRate = (double) confirmedCount / totalAppointments;
        
        long completedCount = countsByStatus.getOrDefault(AppointmentStatus.COMPLETED, 0L);
        double completionRate = (double) completedCount / totalAppointments;
        
        // Construir respuesta detallada
        Map<String, Long> appointmentsByStatusName = new HashMap<>();
        List<AppointmentSummaryResponse.StatusCount> statusCounts = new ArrayList<>();
        
        for (Map.Entry<AppointmentStatus, Long> entry : countsByStatus.entrySet()) {
            AppointmentStatus status = entry.getKey();
            Long count = entry.getValue();
            
            appointmentsByStatusName.put(status.getDisplayName(), count);
            
            statusCounts.add(new AppointmentSummaryResponse.StatusCount(
                    status,
                    status.getDisplayName(),
                    count,
                    (double) count / totalAppointments
            ));
        }
        
        List<AppointmentSummaryResponse.SpecialtyCount> specialtyCounts = new ArrayList<>();
        
        for (Map.Entry<String, Long> entry : countsBySpecialty.entrySet()) {
            String specialtyName = entry.getKey();
            Long count = entry.getValue();
            
            specialtyCounts.add(new AppointmentSummaryResponse.SpecialtyCount(
                    null, // No tenemos ID aquí
                    specialtyName,
                    count,
                    (double) count / totalAppointments
            ));
        }
        
        List<AppointmentSummaryResponse.DoctorCount> doctorCounts = new ArrayList<>();
        
        appointmentsByDoctor.forEach((doctorId, doctorAppointments) -> {
            long totalForDoctor = doctorAppointments.size();
            
            String doctorName = doctorAppointments.get(0).getDoctor().getFirstName() + " " +
                    doctorAppointments.get(0).getDoctor().getLastName();
            
            long completedForDoctor = doctorAppointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                    .count();
            
            long cancelledForDoctor = doctorAppointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                    .count();
            
            long noShowForDoctor = doctorAppointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.NO_SHOW)
                    .count();
            
            doctorCounts.add(new AppointmentSummaryResponse.DoctorCount(
                    doctorId,
                    doctorName,
                    totalForDoctor,
                    (double) totalForDoctor / totalAppointments,
                    completedForDoctor,
                    cancelledForDoctor,
                    noShowForDoctor
            ));
        });
        
        return new AppointmentSummaryResponse(
                totalAppointments,
                appointmentsByStatusName,
                countsBySpecialty,
                cancellationRateByDoctor,
                noShowRate,
                confirmationRate,
                completionRate,
                statusCounts,
                specialtyCounts,
                doctorCounts
        );
    }

    // Métodos de utilidad

    private Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
    }

    private void validateCreateAppointmentRequest(CreateAppointmentRequest request) {
        // Validar fecha futura
        LocalDate today = LocalDate.now();
        if (request.getAppointmentDate().isBefore(today)) {
            throw new ValidationException("La fecha de la cita no puede ser en el pasado");
        }

        // Validar que el doctor pertenezca a la especialidad indicada
        doctorRepository.findById(request.getDoctorId())
                .filter(doctor -> doctor.getSpecialties().stream()
                        .anyMatch(ds -> ds.getSpecialty().getId().equals(request.getSpecialtyId())))
                .orElseThrow(() -> new BusinessException(
                        "El doctor seleccionado no pertenece a la especialidad indicada"));
    }

    private void validateAppointmentStatus(Appointment appointment, AppointmentStatus newStatus) {
        if (newStatus == null) {
            return;
        }

        AppointmentStatus currentStatus = appointment.getStatus();

        if (currentStatus == AppointmentStatus.COMPLETED && 
                newStatus != AppointmentStatus.COMPLETED) {
            throw new BusinessException("No se puede cambiar el estado de una cita ya completada");
        }

        if (currentStatus == AppointmentStatus.CANCELLED && 
                newStatus != AppointmentStatus.CANCELLED && 
                newStatus != AppointmentStatus.RESCHEDULED) {
            throw new BusinessException("No se puede cambiar el estado de una cita cancelada");
        }

        if (currentStatus == AppointmentStatus.NO_SHOW && 
                newStatus != AppointmentStatus.NO_SHOW && 
                newStatus != AppointmentStatus.RESCHEDULED) {
            throw new BusinessException("No se puede cambiar el estado de una cita marcada como no asistida");
        }
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();

        // Datos principales
        response.setId(appointment.getId());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());
        response.setReason(appointment.getReason());
        response.setNotes(appointment.getNotes());
        response.setPrice(appointment.getPrice());

        // Estados
        response.setStatus(appointment.getStatus());
        response.setStatusName(appointment.getStatus().getDisplayName());
        response.setPaymentStatus(appointment.getPaymentStatus());
        response.setPaymentStatusName(appointment.getPaymentStatus().getDisplayName());

        // Tipo de cita
        response.setInPerson(appointment.getInPerson());
        response.setVirtualMeetingUrl(appointment.getVirtualMeetingUrl());

        // Información adicional
        response.setReminderSent(appointment.getReminderSent());
        response.setFollowUpAppointmentId(appointment.getFollowUpAppointmentId());
        response.setCancellationReason(appointment.getCancellationReason());

        // Datos del paciente
        Patient patient = appointment.getPatient();
        if (patient != null) {
            response.setPatientId(patient.getId());
            response.setPatientName(patient.getFirstName() + " " + patient.getLastName());
            if (patient.getDocumentType() != null) {
                response.setPatientDocument(patient.getDocumentNumber() + " - " + patient.getDocumentType().getName());
            } else {
                response.setPatientDocument(patient.getDocumentNumber());
            }
            response.setPatientPhone(patient.getPhone());
            if (patient.getUser() != null) {
                response.setPatientEmail(patient.getUser().getEmail());
            }
        }

        // Datos del doctor
        Doctor doctor = appointment.getDoctor();
        if (doctor != null) {
            response.setDoctorId(doctor.getId());
            response.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
            response.setDoctorPhone(doctor.getPhone());
            if (doctor.getUser() != null) {
                response.setDoctorEmail(doctor.getUser().getEmail());
            }
            response.setDoctorOfficeNumber(doctor.getConsultationRoom());
        }

        // Datos de la especialidad
        Specialty specialty = appointment.getSpecialty();
        if (specialty != null) {
            response.setSpecialtyId(specialty.getId());
            response.setSpecialtyName(specialty.getName());
            response.setAverageDuration(specialty.getAverageDuration());

            // Doctor especialidad preferente
            if (doctor != null && doctor.getSpecialties() != null) {
                doctor.getSpecialties().stream()
                        .filter(ds -> ds.getSpecialty().getId().equals(specialty.getId()) && ds.getIsPrimary())
                        .findFirst()
                        .ifPresent(ds -> response.setDoctorSpecialty(specialty.getName()));
            }
        }

        // Metadatos
        response.setCreatedAt(appointment.getCreatedAt() != null ? 
                appointment.getCreatedAt().toLocalDate() : null);

        return response;
    }
} 