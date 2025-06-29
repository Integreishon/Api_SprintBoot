package com.hospital.backend.appointment.controller;

import com.hospital.backend.appointment.dto.request.CreateAppointmentRequest;
import com.hospital.backend.appointment.dto.request.UpdateAppointmentRequest;
import com.hospital.backend.appointment.dto.response.AppointmentResponse;
import com.hospital.backend.appointment.dto.response.AppointmentSummaryResponse;
import com.hospital.backend.appointment.dto.response.BlockAvailabilityResponse;
import com.hospital.backend.appointment.service.AppointmentService;
import com.hospital.backend.appointment.service.AvailabilityService;
import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.TimeBlock;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de citas médicas
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "📅 Citas Médicas", description = "Gestión completa de citas médicas: programación, confirmación, seguimiento y reportes.")
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
    
    /**
     * Crear una cita virtual (para pacientes del portal web)
     */
    @Operation(summary = "Crear cita virtual con pago pendiente")
    @PostMapping("/virtual")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createVirtualAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAppointmentRequest request) {
        
        log.info("POST /api/appointments/virtual - Creando nueva cita virtual");
        AppointmentResponse response = appointmentService.createVirtualAppointment(userDetails, request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cita virtual creada exitosamente. Por favor, suba el comprobante de pago.", response));
    }
    
    /**
     * Subir comprobante de pago para una cita virtual
     */
    @Operation(summary = "Subir comprobante de pago")
    @PostMapping(value = "/{appointmentId}/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<Boolean>> uploadPaymentReceipt(
            @PathVariable Long appointmentId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("POST /api/appointments/{}/receipt - Subiendo comprobante de pago", appointmentId);
        boolean result = appointmentService.uploadPaymentReceipt(appointmentId, file, userDetails);
        
        return ResponseEntity.ok(ApiResponse.success("Comprobante de pago subido exitosamente. Su cita será validada por nuestro personal.", result));
    }
    
    /**
     * Obtener mis citas (para pacientes del portal web)
     */
    @Operation(summary = "Obtener mis citas")
    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("GET /api/appointments/me - Obteniendo citas del paciente autenticado");
        List<AppointmentResponse> response = appointmentService.getPatientAppointments(userDetails);
        
        return ResponseEntity.ok(ApiResponse.success("Citas obtenidas exitosamente", response));
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
    
    @Operation(summary = "Iniciar consulta")
    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> startConsultation(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        
        log.info("PUT /api/appointments/{}/start - Iniciando consulta", id);
        AppointmentResponse response = appointmentService.startConsultation(id);
        
        return ResponseEntity.ok(ApiResponse.success("Consulta iniciada exitosamente", response));
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
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        
        log.info("PUT /api/appointments/{}/cancel - Cancelando cita", id);
        AppointmentResponse response = appointmentService.cancelAppointment(id);
        
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
    
    @Operation(summary = "Obtener bloques disponibles")
    @GetMapping("/available-blocks/{doctorId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<BlockAvailabilityResponse>>> getAvailableBlocks(
            @Parameter(description = "ID del doctor") @PathVariable Long doctorId,
            @Parameter(description = "Fecha (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("GET /api/appointments/available-blocks/{} - Obteniendo bloques disponibles para doctor", doctorId);
        List<BlockAvailabilityResponse> response = availabilityService.getAvailableBlocks(doctorId, date);
        
        return ResponseEntity.ok(ApiResponse.success("Bloques disponibles obtenidos", response));
    }
    
    @Operation(summary = "Obtener disponibilidad por especialidad")
    @GetMapping("/availability/specialty/{specialtyId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Map<TimeBlock, List<BlockAvailabilityResponse>>>> getAvailabilityBySpecialty(
            @Parameter(description = "ID de la especialidad") @PathVariable Long specialtyId,
            @Parameter(description = "Fecha (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("GET /api/appointments/availability/specialty/{} - Obteniendo disponibilidad por especialidad", specialtyId);
        Map<TimeBlock, List<BlockAvailabilityResponse>> response = availabilityService.getAvailabilityBySpecialty(specialtyId, date);
        
        return ResponseEntity.ok(ApiResponse.success("Disponibilidad por especialidad obtenida", response));
    }
    
    @Operation(summary = "Obtener citas por bloque de tiempo y fecha")
    @GetMapping("/date/{date}/block/{block}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByDateAndTimeBlock(
            @Parameter(description = "Fecha (YYYY-MM-DD)") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Bloque de tiempo") @PathVariable TimeBlock block) {
        
        log.info("GET /api/appointments/date/{}/block/{} - Obteniendo citas por fecha y bloque", date, block);
        List<AppointmentResponse> response = appointmentService.getAppointmentsByDateAndTimeBlock(date, block);
        
        return ResponseEntity.ok(ApiResponse.success("Citas por fecha y bloque obtenidas", response));
    }
    
    @Operation(summary = "Obtener resumen de citas por rango de fechas")
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentSummaryResponse>> getAppointmentSummary(
            @Parameter(description = "Fecha inicio (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha fin (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("GET /api/appointments/summary - Obteniendo resumen de citas");
        AppointmentSummaryResponse response = appointmentService.getAppointmentSummary(startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Resumen de citas obtenido", response));
    }
}
