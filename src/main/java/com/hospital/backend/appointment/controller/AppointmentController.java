package com.hospital.backend.appointment.controller;

import com.hospital.backend.appointment.dto.request.AvailableSlotsRequest;
import com.hospital.backend.appointment.dto.request.CreateAppointmentRequest;
import com.hospital.backend.appointment.dto.request.UpdateAppointmentRequest;
import com.hospital.backend.appointment.dto.response.AppointmentResponse;
import com.hospital.backend.appointment.dto.response.AppointmentSummaryResponse;
import com.hospital.backend.appointment.dto.response.AvailableSlotResponse;
import com.hospital.backend.appointment.service.AppointmentService;
import com.hospital.backend.appointment.service.AvailabilityService;
import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la gestión de citas médicas
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Appointments", description = "Gestión de citas médicas")
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    private final AvailabilityService availabilityService;
    
    // =========================
    // CRUD Operations
    // =========================
    
    @Operation(summary = "Crear nueva cita médica")
    @PostMapping
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request) {
        
        log.info("POST /api/appointments - Creando nueva cita");
        AppointmentResponse response = appointmentService.createAppointment(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cita creada exitosamente", response));
    }
    
    @Operation(summary = "Obtener cita por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        
        log.info("GET /api/appointments/{} - Obteniendo cita por ID", id);
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Cita encontrada", response));
    }
    
    @Operation(summary = "Listar todas las citas con paginación")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentResponse>>> getAllAppointments(
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /api/appointments - Listando citas (página: {}, tamaño: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AppointmentResponse> response = appointmentService.getAllAppointments(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Lista de citas obtenida", response));
    }
    
    @Operation(summary = "Actualizar cita médica")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointment(
            @Parameter(description = "ID de la cita") @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentRequest request) {
        
        log.info("PUT /api/appointments/{} - Actualizando cita", id);
        AppointmentResponse response = appointmentService.updateAppointment(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Cita actualizada exitosamente", response));
    }
    
    // =========================
    // Cambios de estado
    // =========================
    
    @Operation(summary = "Confirmar una cita")
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> confirmAppointment(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        
        log.info("PUT /api/appointments/{}/confirm - Confirmando cita", id);
        AppointmentResponse response = appointmentService.confirmAppointment(id);
        
        return ResponseEntity.ok(ApiResponse.success("Cita confirmada exitosamente", response));
    }
    
    @Operation(summary = "Completar una cita")
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> completeAppointment(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        
        log.info("PUT /api/appointments/{}/complete - Completando cita", id);
        AppointmentResponse response = appointmentService.completeAppointment(id);
        
        return ResponseEntity.ok(ApiResponse.success("Cita completada exitosamente", response));
    }
    
    @Operation(summary = "Cancelar una cita")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(
            @Parameter(description = "ID de la cita") @PathVariable Long id,
            @Parameter(description = "Motivo de cancelación") @RequestParam(required = false) String reason) {
        
        log.info("PUT /api/appointments/{}/cancel - Cancelando cita por: {}", id, reason);
        AppointmentResponse response = appointmentService.cancelAppointment(id, reason);
        
        return ResponseEntity.ok(ApiResponse.success("Cita cancelada exitosamente", response));
    }
    
    @Operation(summary = "Marcar como no asistió")
    @PutMapping("/{id}/no-show")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> markAsNoShow(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        
        log.info("PUT /api/appointments/{}/no-show - Marcando como no asistió", id);
        AppointmentResponse response = appointmentService.markAsNoShow(id);
        
        return ResponseEntity.ok(ApiResponse.success("Cita marcada como no asistió", response));
    }
    
    // =========================
    // Consultas específicas
    // =========================
    
    @Operation(summary = "Obtener citas de un paciente")
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentResponse>>> getAppointmentsByPatient(
            @Parameter(description = "ID del paciente") @PathVariable Long patientId,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /api/appointments/patient/{} - Obteniendo citas del paciente", patientId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AppointmentResponse> response = appointmentService.getAppointmentsByPatient(patientId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Citas del paciente obtenidas", response));
    }
    
    @Operation(summary = "Obtener citas de un doctor")
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentResponse>>> getAppointmentsByDoctor(
            @Parameter(description = "ID del doctor") @PathVariable Long doctorId,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /api/appointments/doctor/{} - Obteniendo citas del doctor", doctorId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AppointmentResponse> response = appointmentService.getAppointmentsByDoctor(doctorId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Citas del doctor obtenidas", response));
    }
    
    @Operation(summary = "Obtener citas de una especialidad")
    @GetMapping("/specialty/{specialtyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentResponse>>> getAppointmentsBySpecialty(
            @Parameter(description = "ID de la especialidad") @PathVariable Long specialtyId,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /api/appointments/specialty/{} - Obteniendo citas de especialidad", specialtyId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AppointmentResponse> response = appointmentService.getAppointmentsBySpecialty(specialtyId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Citas de la especialidad obtenidas", response));
    }
    
    @Operation(summary = "Obtener citas por estado")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentResponse>>> getAppointmentsByStatus(
            @Parameter(description = "Estado de la cita") @PathVariable AppointmentStatus status,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /api/appointments/status/{} - Obteniendo citas por estado", status);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AppointmentResponse> response = appointmentService.getAppointmentsByStatus(status, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Citas por estado obtenidas", response));
    }
    
    @Operation(summary = "Obtener citas de un doctor en una fecha específica")
    @GetMapping("/doctor/{doctorId}/date/{date}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getDoctorAppointmentsByDate(
            @Parameter(description = "ID del doctor") @PathVariable Long doctorId,
            @Parameter(description = "Fecha (YYYY-MM-DD)") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("GET /api/appointments/doctor/{}/date/{} - Obteniendo citas del doctor por fecha", doctorId, date);
        List<AppointmentResponse> response = appointmentService.getDoctorAppointmentsByDate(doctorId, date);
        
        return ResponseEntity.ok(ApiResponse.success("Citas del doctor por fecha obtenidas", response));
    }
    
    // =========================
    // Disponibilidad
    // =========================
    
    @Operation(summary = "Obtener slots disponibles")
    @PostMapping("/available-slots")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<AvailableSlotResponse>>> getAvailableSlots(
            @Valid @RequestBody AvailableSlotsRequest request) {
        
        log.info("POST /api/appointments/available-slots - Obteniendo slots disponibles para doctor: {}, fecha: {}", 
                request.getDoctorId(), request.getDate());
        
        List<AvailableSlotResponse> response = availabilityService.getAvailableSlots(request);
        
        return ResponseEntity.ok(ApiResponse.success("Slots disponibles obtenidos", response));
    }
    
    @Operation(summary = "Obtener slots disponibles por parámetros GET")
    @GetMapping("/available-slots")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<AvailableSlotResponse>>> getAvailableSlotsGet(
            @Parameter(description = "ID del doctor") @RequestParam Long doctorId,
            @Parameter(description = "ID de la especialidad") @RequestParam Long specialtyId,
            @Parameter(description = "Fecha (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("GET /api/appointments/available-slots - Obteniendo slots disponibles para doctor: {}, fecha: {}", 
                doctorId, date);
        
        AvailableSlotsRequest request = new AvailableSlotsRequest(doctorId, specialtyId, date);
        List<AvailableSlotResponse> response = availabilityService.getAvailableSlots(request);
        
        return ResponseEntity.ok(ApiResponse.success("Slots disponibles obtenidos", response));
    }
    
    // =========================
    // Estadísticas y reportes
    // =========================
    
    @Operation(summary = "Obtener resumen de citas por rango de fechas")
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentSummaryResponse>> getAppointmentSummary(
            @Parameter(description = "Fecha inicio (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("GET /api/appointments/summary - Obteniendo resumen desde {} hasta {}", startDate, endDate);
        AppointmentSummaryResponse response = appointmentService.getAppointmentSummary(startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Resumen de citas obtenido", response));
    }
}
