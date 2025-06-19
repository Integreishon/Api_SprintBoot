package com.hospital.backend.payment.service;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.catalog.entity.PaymentMethod;
import com.hospital.backend.catalog.repository.PaymentMethodRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.PaymentMethodType;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.payment.dto.CreatePaymentRequest;
import com.hospital.backend.payment.dto.PaymentResponse;
import com.hospital.backend.payment.dto.PaymentSummaryResponse;
import com.hospital.backend.payment.entity.Payment;
import com.hospital.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
@Slf4j
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
        log.info("Creando nuevo pago para cita: {}", request.getAppointmentId());
        
        // Buscar la cita
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", request.getAppointmentId()));
        
        // Verificar que la cita no tenga ya un pago
        paymentRepository.findByAppointmentId(appointment.getId())
                .ifPresent(p -> {
                    throw new BusinessException("La cita ya tiene un pago registrado");
                });
        
        // Buscar el método de pago
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", request.getPaymentMethodId()));
        
        // Verificar que el método de pago esté activo
        if (!paymentMethod.getIsActive()) {
            throw new BusinessException("El método de pago seleccionado no está disponible");
        }
        
        // Calcular monto y comisión
        BigDecimal amount = request.getAmount() != null ? request.getAmount() : appointment.getPrice();
        BigDecimal processingFee = calculateProcessingFee(amount, paymentMethod);
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
        payment.setTransactionReference(request.getTransactionReference());
        
        // Calcular el total incluyendo fees
        payment.calculateTotalAmount();
        
        // Si viene con referencia de transacción, marcar como pagado automáticamente
        if (request.getTransactionReference() != null && !request.getTransactionReference().isEmpty()) {
            payment.markAsPaid(request.getTransactionReference());
            log.info("Pago marcado como completado automáticamente con referencia: {}", request.getTransactionReference());
        }
        
        // Generar número de recibo
        payment.setReceiptNumber(generateReceiptNumber());
        
        // Guardar el pago
        payment = paymentRepository.save(payment);
        
        log.info("Pago creado exitosamente con ID: {}", payment.getId());
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Confirma un pago pendiente
     */
    @Transactional
    public PaymentResponse confirmPayment(Long id, String transactionReference) {
        log.info("Confirmando pago con ID: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Solo se pueden confirmar pagos pendientes");
        }
        
        // Marcar pago como pagado
        payment.markAsPaid(transactionReference);
        payment = paymentRepository.save(payment);
        
        log.info("Pago confirmado exitosamente");
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Marca un pago como fallido
     */
    @Transactional
    public PaymentResponse markPaymentAsFailed(Long id) {
        log.info("Marcando pago como fallido con ID: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Solo se pueden marcar como fallidos los pagos pendientes");
        }
        
        payment.markAsFailed();
        payment = paymentRepository.save(payment);
        
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Reembolsa un pago
     */
    @Transactional
    public PaymentResponse refundPayment(Long id) {
        log.info("Procesando reembolso para pago con ID: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        
        if (!payment.canRefund()) {
            throw new BusinessException("Este pago no puede ser reembolsado. Estado actual: " + payment.getStatus());
        }
        
        // Marcar como reembolsado
        payment.markAsRefunded();
        payment = paymentRepository.save(payment);
        
        log.info("Reembolso procesado exitosamente");
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Obtiene un pago por su ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Obtiene un pago por ID de cita
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByAppointment(Long appointmentId) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe pago para esta cita"));
        
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Lista pagos paginados
     */
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getAllPayments(Pageable pageable) {
        Page<Payment> paymentsPage = paymentRepository.findAll(pageable);
        Page<PaymentResponse> mappedPage = paymentsPage.map(this::mapToPaymentResponse);
        return new PageResponse<>(mappedPage);
    }
    
    /**
     * Lista pagos por estado
     */
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        Page<Payment> paymentsPage = paymentRepository.findByStatus(status, pageable);
        Page<PaymentResponse> mappedPage = paymentsPage.map(this::mapToPaymentResponse);
        return new PageResponse<>(mappedPage);
    }
    
    /**
     * Obtiene pagos por rango de fechas
     */
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        Page<Payment> paymentsPage = paymentRepository.findByPaymentDateBetween(startDateTime, endDateTime, pageable);
        Page<PaymentResponse> mappedPage = paymentsPage.map(this::mapToPaymentResponse);
        return new PageResponse<>(mappedPage);
    }
    
    /**
     * Genera reportes de pagos por período
     */
    @Transactional(readOnly = true)
    public PaymentSummaryResponse generatePaymentSummary(LocalDate startDate, LocalDate endDate) {
        log.info("Generando resumen de pagos desde {} hasta {}", startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Obtener todos los pagos en el rango de fechas
        List<Payment> payments = paymentRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        
        // Inicializar contadores y totales
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
        long failedCount = 0;
        
        for (Payment payment : payments) {
            PaymentStatus status = payment.getStatus();
            
            // Contar por estado
            switch (status) {
                case COMPLETED:
                    completedCount++;
                    totalRevenue = totalRevenue.add(payment.getAmount());
                    totalFees = totalFees.add(payment.getProcessingFee() != null ? payment.getProcessingFee() : BigDecimal.ZERO);
                    
                    // Clasificar por tipo de pago
                    BigDecimal amount = payment.getTotalAmount();
                    PaymentMethodType type = payment.getPaymentMethod().getType();
                    
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
                        default:
                            onlinePayments = onlinePayments.add(amount);
                            break;
                    }
                    break;
                case PENDING:
                    pendingCount++;
                    break;
                case REFUNDED:
                    refundedCount++;
                    break;
                case FAILED:
                    failedCount++;
                    break;
            }
        }
        
        // Calcular ingresos netos
        BigDecimal netRevenue = totalRevenue.subtract(totalFees);
        
        // Calcular promedio por transacción
        BigDecimal averageAmount = completedCount > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(completedCount), 2, RoundingMode.HALF_UP) : 
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
                .failedPayments(failedCount)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
    
    // =========================
    // Métodos privados auxiliares
    // =========================
    
    /**
     * Calcula la comisión de procesamiento
     */
    private BigDecimal calculateProcessingFee(BigDecimal amount, PaymentMethod paymentMethod) {
        if (paymentMethod.getProcessingFee() == null) {
            return BigDecimal.ZERO;
        }
        
        return amount.multiply(paymentMethod.getProcessingFee())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
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
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
