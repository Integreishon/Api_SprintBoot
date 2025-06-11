package com.hospital.backend.security.service;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio de seguridad específico para validaciones de citas médicas
 */
@Service("appointmentSecurityService")
@RequiredArgsConstructor
@Slf4j
public class AppointmentSecurityService {

    private final AppointmentRepository appointmentRepository;

    /**
     * Verifica si el usuario actual es el paciente de la cita
     */
    public boolean isAppointmentPatient(Long appointmentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("No hay usuario autenticado");
                return false;
            }

            Object principal = authentication.getPrincipal();
            
            if (!(principal instanceof UserPrincipal)) {
                log.debug("Principal no es UserPrincipal");
                return false;
            }

            UserPrincipal userPrincipal = (UserPrincipal) principal;
            
            // Buscar la cita
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            
            if (appointmentOpt.isEmpty()) {
                log.debug("Cita no encontrada: {}", appointmentId);
                return false;
            }

            Appointment appointment = appointmentOpt.get();
            
            // TODO: Verificar que el userId corresponde al patientId
            // Por ahora, asumimos que el user.id es igual al patient.user_id
            Long patientUserId = appointment.getPatient().getUser().getId();
            boolean isPatient = userPrincipal.getId().equals(patientUserId);
            
            log.debug("Verificando si usuario {} es paciente de cita {}: {}", 
                    userPrincipal.getId(), appointmentId, isPatient);
            
            return isPatient;
            
        } catch (Exception e) {
            log.error("Error verificando paciente de cita: ", e);
            return false;
        }
    }

    /**
     * Verifica si el usuario actual es el doctor de la cita
     */
    public boolean isAppointmentDoctor(Long appointmentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            Object principal = authentication.getPrincipal();
            
            if (!(principal instanceof UserPrincipal)) {
                return false;
            }

            UserPrincipal userPrincipal = (UserPrincipal) principal;
            
            // Buscar la cita
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            
            if (appointmentOpt.isEmpty()) {
                log.debug("Cita no encontrada: {}", appointmentId);
                return false;
            }

            Appointment appointment = appointmentOpt.get();
            
            // TODO: Verificar que el userId corresponde al doctorId
            // Por ahora, asumimos que el user.id es igual al doctor.user_id
            Long doctorUserId = appointment.getDoctor().getUser().getId();
            boolean isDoctor = userPrincipal.getId().equals(doctorUserId);
            
            log.debug("Verificando si usuario {} es doctor de cita {}: {}", 
                    userPrincipal.getId(), appointmentId, isDoctor);
            
            return isDoctor;
            
        } catch (Exception e) {
            log.error("Error verificando doctor de cita: ", e);
            return false;
        }
    }

    /**
     * Verifica si el usuario puede ver la cita (paciente, doctor, admin)
     */
    public boolean canViewAppointment(Long appointmentId) {
        try {
            // Administradores pueden ver cualquier cita
            if (hasRole("ADMIN") || hasRole("RECEPTIONIST")) {
                return true;
            }

            // Verificar si es el paciente o doctor de la cita
            return isAppointmentPatient(appointmentId) || isAppointmentDoctor(appointmentId);
            
        } catch (Exception e) {
            log.error("Error verificando permisos de vista de cita: ", e);
            return false;
        }
    }

    /**
     * Verifica si el usuario puede modificar la cita
     */
    public boolean canModifyAppointment(Long appointmentId) {
        try {
            // Administradores y recepcionistas pueden modificar cualquier cita
            if (hasRole("ADMIN") || hasRole("RECEPTIONIST")) {
                return true;
            }

            // Doctores pueden modificar sus propias citas
            if (hasRole("DOCTOR") && isAppointmentDoctor(appointmentId)) {
                return true;
            }

            // Pacientes pueden modificar sus propias citas (con restricciones)
            if (hasRole("PATIENT") && isAppointmentPatient(appointmentId)) {
                return true;
            }

            return false;
            
        } catch (Exception e) {
            log.error("Error verificando permisos de modificación de cita: ", e);
            return false;
        }
    }

    private boolean hasRole(String role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role.toUpperCase()));
                    
        } catch (Exception e) {
            log.error("Error verificando rol en appointment security: ", e);
            return false;
        }
    }
}