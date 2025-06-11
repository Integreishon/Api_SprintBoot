package com.hospital.backend.payment.service;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.catalog.entity.PaymentMethod;
import com.hospital.backend.catalog.repository.PaymentMethodRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.PaymentMethodType;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.payment.dto.CreatePaymentRequest;
import com.hospital.backend.payment.dto.PaymentResponse;
import com.hospital.backend.payment.dto.PaymentSummaryResponse;
import com.hospital.backend.payment.entity.Payment;
import com.hospital.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para la gestión de pagos
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    
    private static final DateTimeFormatter RECEIPT_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    /**
     * Crea un nuevo pago para una cita
     */
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        // Buscar la cita
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        
        // Verificar que la cita no tenga ya un pago
        paymentRepository.findByAppointmentId(appointment.getId())
                .ifPresent(p -> {
                    throw new BusinessException("La cita ya tiene un pago registrado");
                });
        
        // Buscar el método de pago
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado"));
        
        // Verificar que el método de pago esté activo
        if (!paymentMethod.getIsActive()) {
            throw new BusinessException("El método de pago seleccionado no está disponible");
        }
        
        // Calcular monto y comisión
        BigDecimal amount = request.getAmount() != null ? request.getAmount() : appointment.getPrice();
        BigDecimal processingFee = BigDecimal.ZERO;
        
        if (paymentMethod.getProcessingFee() != null) {
            processingFee = amount.multiply(paymentMethod.getProcessingFee().divide(new BigDecimal(100)));
        }
        
        BigDecimal totalAmount = amount.add(processingFee);
        
        // Crear el pago
        Payment payment = new Payment();
        payment.setAppointment(appointment);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amount);
        payment.setProcessingFee(processingFee);
        payment.setTotalAmount(totalAmount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPayerName(request.getPayerName());
        payment.setPayerEmail(request.getPayerEmail());
        payment.setNotes(request.getNotes());
        payment.setTransactionReference(request.getTransactionReference());
        
        // Si viene con referencia de transacción, marcar como pagado
        if (request.getTransactionReference() != null && !request.getTransactionReference().isEmpty()) {
            payment.markAsPaid(request.getTransactionReference());
            
            // Actualizar estado de la cita a confirmada
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointment.setPaymentStatus(PaymentStatus.COMPLETED);
            appointmentRepository.save(appointment);
        }
        
        // Generar número de recibo
        payment.setReceiptNumber(generateReceiptNumber());
        
        // Guardar el pago
        payment = paymentRepository.save(payment);
        
        // Retornar respuesta
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Confirma un pago pendiente
     */
    @Transactional
    public PaymentResponse confirmPayment(Long id, String transactionReference) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Solo se pueden confirmar pagos pendientes");
        }
        
        // Marcar pago como pagado
        payment.markAsPaid(transactionReference);
        payment = paymentRepository.save(payment);
        
        // Actualizar estado de la cita
        Appointment appointment = payment.getAppointment();
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setPaymentStatus(PaymentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Reembolsa un pago
     */
    @Transactional
    public PaymentResponse refundPayment(Long id, String notes) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        
        if (!payment.canRefund()) {
            throw new BusinessException("Este pago no puede ser reembolsado");
        }
        
        // Marcar como reembolsado
        payment.markAsRefunded(notes);
        payment = paymentRepository.save(payment);
        
        // Actualizar estado de la cita
        Appointment appointment = payment.getAppointment();
        appointment.setPaymentStatus(PaymentStatus.REFUNDED);
        appointmentRepository.save(appointment);
        
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Obtiene un pago por su ID
     */
    public PaymentResponse getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Obtiene un pago por ID de cita
     */
    public PaymentResponse getPaymentByAppointment(Long appointmentId) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe pago para esta cita"));
        
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Lista pagos paginados
     */
    public PageResponse<PaymentResponse> getAllPayments(Pageable pageable) {
        Page<Payment> paymentsPage = paymentRepository.findAll(pageable);
        Page<PaymentResponse> paymentResponses = paymentsPage.map(this::mapToPaymentResponse);
        return new PageResponse<>(paymentResponses);
    }
    
    /**
     * Lista pagos por estado
     */
    public PageResponse<PaymentResponse> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        Page<Payment> paymentsPage = paymentRepository.findByStatus(status, pageable);
        Page<PaymentResponse> paymentResponses = paymentsPage.map(this::mapToPaymentResponse);
        return new PageResponse<>(paymentResponses);
    }
    
    /**
     * Genera reportes de pagos por período
     */
    public PaymentSummaryResponse generatePaymentSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Obtener todos los pagos en el rango de fechas
        List<Payment> payments = paymentRepository.findByPaymentDateBetween(startDateTime, endDateTime);
        
        // Calcular totales
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalFees = BigDecimal.ZERO;
        
        BigDecimal cashPayments = BigDecimal.ZERO;
        BigDecimal cardPayments = BigDecimal.ZERO;
        BigDecimal transferPayments = BigDecimal.ZERO;
        BigDecimal insurancePayments = BigDecimal.ZERO;
        BigDecimal onlinePayments = BigDecimal.ZERO;
        
        long completedCount = 0;
        long pendingCount = 0;
        long refundedCount = 0;
        long cancelledCount = 0;
        
        for (Payment payment : payments) {
            // Sólo considerar pagos completados para ingresos
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                totalRevenue = totalRevenue.add(payment.getAmount());
                totalFees = totalFees.add(payment.getProcessingFee() != null ? payment.getProcessingFee() : BigDecimal.ZERO);
                
                // Clasificar por tipo de pago
                PaymentMethodType type = payment.getPaymentMethod().getType();
                BigDecimal amount = payment.getTotalAmount();
                
                switch (type) {
                    case CASH:
                        cashPayments = cashPayments.add(amount);
                        break;
                    case CREDIT_CARD:
                    case DEBIT_CARD:
                        cardPayments = cardPayments.add(amount);
                        break;
                    case BANK_TRANSFER:
                        transferPayments = transferPayments.add(amount);
                        break;
                    case INSURANCE:
                        insurancePayments = insurancePayments.add(amount);
                        break;
                    case ONLINE:
                        onlinePayments = onlinePayments.add(amount);
                        break;
                }
                
                completedCount++;
            } else if (payment.getStatus() == PaymentStatus.PENDING) {
                pendingCount++;
            } else if (payment.getStatus() == PaymentStatus.REFUNDED) {
                refundedCount++;
            } else if (payment.getStatus() == PaymentStatus.FAILED) {
                cancelledCount++;
            }
        }
        
        // Calcular ingresos netos
        BigDecimal netRevenue = totalRevenue.subtract(totalFees);
        
        // Calcular promedio por transacción
        BigDecimal averageAmount = completedCount > 0 ? 
                totalRevenue.divide(new BigDecimal(completedCount), BigDecimal.ROUND_HALF_UP) : 
                BigDecimal.ZERO;
        
        // Crear y retornar el resumen
        return PaymentSummaryResponse.builder()
                .totalRevenue(totalRevenue)
                .totalFees(totalFees)
                .netRevenue(netRevenue)
                .totalTransactions((long) payments.size())
                .averageTransactionAmount(averageAmount)
                .cashPayments(cashPayments)
                .cardPayments(cardPayments)
                .transferPayments(transferPayments)
                .insurancePayments(insurancePayments)
                .onlinePayments(onlinePayments)
                .completedPayments(completedCount)
                .pendingPayments(pendingCount)
                .refundedPayments(refundedCount)
                .cancelledPayments(cancelledCount)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
    
    /**
     * Genera un número de recibo único
     */
    private String generateReceiptNumber() {
        LocalDateTime now = LocalDateTime.now();
        return "REC-" + now.format(RECEIPT_FORMAT);
    }
    
    /**
     * Mapea la entidad Payment a su DTO PaymentResponse
     */
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        Appointment appointment = payment.getAppointment();
        
        return PaymentResponse.builder()
                .id(payment.getId())
                .appointmentId(appointment.getId())
                .patientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName())
                .doctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName())
                .specialty(appointment.getSpecialty().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getStartTime())
                .paymentMethodName(payment.getPaymentMethod().getName())
                .paymentMethodType(payment.getPaymentMethod().getType())
                .amount(payment.getAmount())
                .processingFee(payment.getProcessingFee())
                .totalAmount(payment.getTotalAmount())
                .transactionReference(payment.getTransactionReference())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .receiptNumber(payment.getReceiptNumber())
                .payerName(payment.getPayerName())
                .payerEmail(payment.getPayerEmail())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}