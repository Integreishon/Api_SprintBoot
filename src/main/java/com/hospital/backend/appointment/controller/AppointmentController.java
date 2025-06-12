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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la gestión de citas médicas
 * VERSIÓN TEMPORAL SIN PreAuthorize COMPLEJOS PARA DEBUGGING
 */
@RestController
@RequestMapping("/appointments")
@Tag(name = "📅 Citas Médicas", description = "Sistema completo de agendamiento de citas: creación, consulta, cancelación, y verificación de disponibilidad de horarios.")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AvailabilityService availabilityService;

    @GetMapping
    @Operation(summary = "Listar todas las citas", description = "Obtiene una lista paginada de todas las citas")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<AppointmentResponse>> findAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una cita por ID", description = "Retorna una cita por su ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Listar citas por paciente", description = "Obtiene una lista paginada de citas de un paciente")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<AppointmentResponse>> findByPatient(
            @PathVariable Long patientId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findByPatient(patientId, pageable));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Listar citas por doctor", description = "Obtiene una lista paginada de citas de un doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<AppointmentResponse>> findByDoctor(
            @PathVariable Long doctorId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findByDoctor(doctorId, pageable));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Listar citas por fecha", description = "Obtiene una lista paginada de citas en una fecha específica")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<AppointmentResponse>> findByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findByDate(date, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar citas por estado", description = "Obtiene una lista paginada de citas con un estado específico")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<AppointmentResponse>> findByStatus(
            @PathVariable AppointmentStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findByStatus(status, pageable));
    }

    @PostMapping
    @Operation(summary = "Crear una cita", description = "Crea una nueva cita")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> create(
            @RequestBody @Valid CreateAppointmentRequest request) {
        AppointmentResponse createdAppointment = appointmentService.create(request);
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una cita", description = "Actualiza una cita existente")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cita", description = "Elimina una cita por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Cita eliminada correctamente")
        );
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar una cita", description = "Cancela una cita existente")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable Long id,
            @Parameter(description = "Motivo de cancelación") @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(appointmentService.cancel(id, reason));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Completar una cita", description = "Marca una cita como completada")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentResponse> complete(
            @PathVariable Long id,
            @Parameter(description = "Notas médicas") @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(appointmentService.complete(id, notes));
    }

    @GetMapping("/patient/{patientId}/upcoming")
    @Operation(summary = "Obtener próximas citas de un paciente", description = "Retorna las próximas citas para un paciente (próximos 7 días)")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> findUpcomingAppointmentsForPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.findUpcomingAppointmentsForPatient(patientId));
    }

    @GetMapping("/doctor/{doctorId}/today")
    @Operation(summary = "Obtener citas del día para un doctor", description = "Retorna las citas del día actual para un doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> findTodayAppointmentsForDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.findTodayAppointmentsForDoctor(doctorId));
    }

    @GetMapping("/summary")
    @Operation(summary = "Generar resumen de citas", description = "Genera estadísticas resumen de citas por periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentSummaryResponse> generateSummary(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseEntity.ok(appointmentService.generateSummary(startDate, endDate));
    }

    @PostMapping("/available-slots")
    @Operation(summary = "Buscar slots disponibles", description = "Busca slots de tiempo disponibles para un doctor y especialidad en una fecha")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'RECEPTIONIST')")
    public ResponseEntity<AvailableSlotResponse> findAvailableSlots(
            @RequestBody @Valid AvailableSlotsRequest request) {
        return ResponseEntity.ok(availabilityService.findAvailableSlots(request));
    }
}