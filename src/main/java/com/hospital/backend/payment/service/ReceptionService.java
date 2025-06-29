package com.hospital.backend.payment.service;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.repository.UserRepository;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.payment.dto.response.PendingValidationResponse;
import com.hospital.backend.payment.entity.Payment;
import com.hospital.backend.payment.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión del portal de validación de citas virtuales
 * Usado por recepcionistas para validar pagos y marcar llegada de pacientes
 */
@Service
@RequiredArgsConstructor
public class ReceptionService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    
    /**
     * Obtiene la lista de citas pendientes de validación
     * Una cita está pendiente si su pago está en PROCESSING y tiene un comprobante subido
     * @return Lista de citas pendientes de validación
     */
    public List<PendingValidationResponse> getAppointmentsPendingValidation() {
        List<Payment> pendingPayments = paymentRepository.findByStatusAndReceiptImagePathIsNotNull(PaymentStatus.PROCESSING);
        
        return pendingPayments.stream()
            .map(payment -> PendingValidationResponse.builder()
                .paymentId(payment.getId())
                .appointmentId(payment.getAppointment().getId())
                .patientName(payment.getAppointment().getPatient().getFirstName() + " " + 
                            payment.getAppointment().getPatient().getLastName())
                .doctorName(payment.getAppointment().getDoctor().getFirstName() + " " + 
                           payment.getAppointment().getDoctor().getLastName())
                .specialtyName(payment.getAppointment().getSpecialty().getName())
                .appointmentDate(payment.getAppointment().getAppointmentDate())
                .timeBlock(payment.getAppointment().getTimeBlock())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().getName())
                .receiptImagePath(payment.getReceiptImagePath())
                .createdAt(payment.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }
    
    /**
     * Valida un pago pendiente
     * Actualiza el estado del pago y de la cita a COMPLETED
     * @param paymentId ID del pago a validar
     * @param userDetails Detalles del usuario (recepcionista) que realiza la validación
     * @return true si la validación fue exitosa
     */
    @Transactional
    public boolean validatePayment(Long paymentId, UserDetails userDetails) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));
        
        if (payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("El pago no está en estado PROCESSING");
        }
        
        User receptionist = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        // Actualizar el pago
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setValidatedByUser(receptionist);
        paymentRepository.save(payment);
        
        // Actualizar la cita
        Appointment appointment = payment.getAppointment();
        appointment.setPaymentStatus(PaymentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        
        return true;
    }
    
    /**
     * Rechaza un pago pendiente
     * @param paymentId ID del pago a rechazar
     * @param reason Motivo del rechazo
     * @return true si el rechazo fue exitoso
     */
    @Transactional
    public boolean rejectPayment(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));
        
        if (payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("El pago no está en estado PROCESSING");
        }
        
        // Actualizar el pago
        payment.setStatus(PaymentStatus.FAILED);
        payment.setTransactionReference("RECHAZADO: " + reason);
        paymentRepository.save(payment);
        
        // Actualizar la cita
        Appointment appointment = payment.getAppointment();
        appointment.setPaymentStatus(PaymentStatus.FAILED);
        appointmentRepository.save(appointment);
        
        return true;
    }
} 