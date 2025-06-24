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
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.enums.TimeBlock;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de citas médicas
 * Adaptado a la nueva lógica con bloques de tiempo y pago obligatorio
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final AvailabilityService availabilityService;
    
    // =========================
    // CRUD Operations
    // =========================
    
    /**
     * Crear una nueva cita médica
     * NOTA: Según nueva lógica, solo se crea con pago confirmado
     */
    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        log.info("Creando nueva cita para paciente: {}, doctor: {}, fecha: {}", 
                request.getPatientId(), request.getDoctorId(), request.getAppointmentDate());
        
        // 1. Validar que las entidades existan
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.getPatientId()));
        
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));
        
        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", "id", request.getSpecialtyId()));
        
        // 2. Validar que el doctor tenga esa especialidad
        validateDoctorSpecialty(doctor, specialty);
        
        // 3. Validar disponibilidad del doctor (por bloque)
        validateDoctorAvailability(doctor.getId(), request.getAppointmentDate(), request.getTimeBlock());
        
        // 4. Validar si la especialidad requiere derivación
        if (specialty.getRequiresReferral() != null && specialty.getRequiresReferral() 
                && (request.getReferralId() == null)) {
            throw new BusinessException("Esta especialidad requiere derivación médica");
        }
        
        // 5. Crear la cita (siempre con pago confirmado)
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSpecialty(specialty);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setTimeBlock(request.getTimeBlock());
        appointment.setReason(request.getReason());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPaymentStatus(PaymentStatus.COMPLETED);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Cita creada exitosamente con ID: {}", savedAppointment.getId());
        
        return mapToResponse(savedAppointment);
    }
    
    /**
     * Actualizar una cita existente
     */
    @Transactional
    public AppointmentResponse updateAppointment(Long id, UpdateAppointmentRequest request) {
        log.info("Actualizando cita con ID: {}", id);
        
        Appointment appointment = findAppointmentWithDetails(id);
        
        // Validar que se puede modificar (solo si está programada)
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("No se puede modificar una cita con estado: " + appointment.getStatus());
        }
        
        // Actualizar campos si se proporcionan
        if (request.getAppointmentDate() != null) {
            appointment.setAppointmentDate(request.getAppointmentDate());
        }
        
        if (request.getTimeBlock() != null) {
            appointment.setTimeBlock(request.getTimeBlock());
            // Validar nueva disponibilidad
            validateDoctorAvailability(appointment.getDoctor().getId(), 
                                     appointment.getAppointmentDate(),
                                     request.getTimeBlock());
        }
        
        if (request.getReason() != null && !request.getReason().trim().isEmpty()) {
            appointment.setReason(request.getReason());
        }
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Cita actualizada exitosamente");
        
        return mapToResponse(savedAppointment);
    }
    
    /**
     * Obtener cita por ID
     */
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = findAppointmentWithDetails(id);
        return mapToResponse(appointment);
    }
    
    /**
     * Listar todas las citas con paginación
     */
    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAllAppointments(Pageable pageable) {
        Page<Appointment> appointmentPage = appointmentRepository.findAll(pageable);
        Page<AppointmentResponse> mappedPage = appointmentPage.map(this::mapToResponse);
        return new PageResponse<>(mappedPage);
    }
    
    // =========================
    // Cambios de estado
    // =========================
    
    /**
     * Iniciar consulta
     */
    @Transactional
    public AppointmentResponse startConsultation(Long id) {
        log.info("Iniciando consulta con ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Solo se pueden iniciar consultas programadas");
        }
        
        appointment.setStatus(AppointmentStatus.IN_CONSULTATION);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        return mapToResponse(savedAppointment);
    }
    
    /**
     * Completar una cita
     */
    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        log.info("Completando cita con ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        if (appointment.getStatus() != AppointmentStatus.IN_CONSULTATION) {
            throw new BusinessException("Solo se pueden completar citas en consulta");
        }
        
        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        return mapToResponse(savedAppointment);
    }
    
    /**
     * Cancelar una cita
     */
    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {
        log.info("Cancelando cita con ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        if (!appointment.canCancel()) {
            throw new BusinessException("No se puede cancelar una cita con estado: " + appointment.getStatus());
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        return mapToResponse(savedAppointment);
    }
    
    /**
     * Marcar como no asistió
     */
    @Transactional
    public AppointmentResponse markAsNoShow(Long id) {
        log.info("Marcando como no asistió la cita con ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Solo se pueden marcar como no asistió las citas programadas");
        }
        
        appointment.setStatus(AppointmentStatus.NO_SHOW);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        return mapToResponse(savedAppointment);
    }
    
    // =========================
    // Consultas específicas
    // =========================
    
    /**
     * Obtener citas de un paciente
     */
    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAppointmentsByPatient(Long patientId, Pageable pageable) {
        // Verificar que el paciente exista
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }
        
        Page<Appointment> appointmentPage = appointmentRepository.findByPatientId(patientId, pageable);
        Page<AppointmentResponse> mappedPage = appointmentPage.map(this::mapToResponse);
        return new PageResponse<>(mappedPage);
    }
    
    /**
     * Obtener citas de un doctor
     */
    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAppointmentsByDoctor(Long doctorId, Pageable pageable) {
        // Verificar que el doctor exista
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }
        
        Page<Appointment> appointmentPage = appointmentRepository.findByDoctorId(doctorId, pageable);
        Page<AppointmentResponse> mappedPage = appointmentPage.map(this::mapToResponse);
        return new PageResponse<>(mappedPage);
    }
    
    /**
     * Obtener citas por especialidad
     */
    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAppointmentsBySpecialty(Long specialtyId, Pageable pageable) {
        // Verificar que la especialidad exista
        if (!specialtyRepository.existsById(specialtyId)) {
            throw new ResourceNotFoundException("Specialty", "id", specialtyId);
        }
        
        Page<Appointment> appointmentPage = appointmentRepository.findBySpecialtyId(specialtyId, pageable);
        Page<AppointmentResponse> mappedPage = appointmentPage.map(this::mapToResponse);
        return new PageResponse<>(mappedPage);
    }
    
    /**
     * Obtener citas por estado
     */
    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable) {
        Page<Appointment> appointmentPage = appointmentRepository.findByStatus(status, pageable);
        Page<AppointmentResponse> mappedPage = appointmentPage.map(this::mapToResponse);
        return new PageResponse<>(mappedPage);
    }
    
    /**
     * Obtener citas de un doctor por fecha
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDoctorAppointmentsByDate(Long doctorId, LocalDate date) {
        // Verificar que el doctor exista
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }
        
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener citas por bloque de tiempo y fecha
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDateAndTimeBlock(LocalDate date, TimeBlock timeBlock) {
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateAndTimeBlock(date, timeBlock);
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener resumen de citas
     */
    @Transactional(readOnly = true)
    public AppointmentSummaryResponse getAppointmentSummary(LocalDate startDate, LocalDate endDate) {
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        if (startDate == null) {
            startDate = endDate.minusDays(30);
        }
        
        long total = appointmentRepository.countByAppointmentDateBetween(startDate, endDate);
        long completed = appointmentRepository.countByStatusAndAppointmentDateBetween(
                AppointmentStatus.COMPLETED, startDate, endDate);
        long cancelled = appointmentRepository.countByStatusAndAppointmentDateBetween(
                AppointmentStatus.CANCELLED, startDate, endDate);
        long noShow = appointmentRepository.countByStatusAndAppointmentDateBetween(
                AppointmentStatus.NO_SHOW, startDate, endDate);
        long scheduled = appointmentRepository.countByStatusAndAppointmentDateBetween(
                AppointmentStatus.SCHEDULED, startDate, endDate);
        long inConsultation = appointmentRepository.countByStatusAndAppointmentDateBetween(
                AppointmentStatus.IN_CONSULTATION, startDate, endDate);
        
        return AppointmentSummaryResponse.builder()
                .totalAppointments(total)
                .completedAppointments(completed)
                .cancelledAppointments(cancelled)
                .noShowAppointments(noShow)
                .scheduledAppointments(scheduled)
                .inConsultationAppointments(inConsultation)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
    
    // =========================
    // Métodos auxiliares
    // =========================
    
    /**
     * Buscar cita con detalles
     */
    private Appointment findAppointmentWithDetails(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
    }
    
    /**
     * Validar que el doctor tenga la especialidad
     */
    private void validateDoctorSpecialty(Doctor doctor, Specialty specialty) {
        boolean hasDoctorSpecialty = doctor.getSpecialties().stream()
                .anyMatch(ds -> ds.getSpecialty().getId().equals(specialty.getId()));
        
        if (!hasDoctorSpecialty) {
            throw new BusinessException(
                    String.format("El doctor %s %s no tiene la especialidad %s", 
                            doctor.getFirstName(), doctor.getLastName(), specialty.getName())
            );
        }
    }
    
    /**
     * Validar disponibilidad del doctor por bloque
     */
    private void validateDoctorAvailability(Long doctorId, LocalDate date, TimeBlock timeBlock) {
        // Verificar si el doctor está disponible en ese bloque
        boolean isAvailable = availabilityService.isDoctorAvailableInTimeBlock(doctorId, date, timeBlock);
        
        if (!isAvailable) {
            throw new BusinessException(
                    String.format("El doctor no está disponible en la fecha %s, bloque %s", 
                            date, timeBlock.getDisplayName())
            );
        }
        
        // Verificar capacidad del bloque
        boolean hasCapacity = availabilityService.hasBlockCapacity(doctorId, date, timeBlock);
        
        if (!hasCapacity) {
            throw new BusinessException(
                    String.format("El bloque %s del %s ya está a capacidad máxima", 
                            timeBlock.getDisplayName(), date)
            );
        }
    }
    
    /**
     * Mapear entidad a DTO de respuesta
     */
    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName())
                .specialtyId(appointment.getSpecialty().getId())
                .specialtyName(appointment.getSpecialty().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .timeBlock(appointment.getTimeBlock())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .paymentStatus(appointment.getPaymentStatus())
                .price(appointment.getPrice())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
