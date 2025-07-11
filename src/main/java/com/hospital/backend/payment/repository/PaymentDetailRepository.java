package com.hospital.backend.payment.repository;

import com.hospital.backend.payment.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para operaciones con detalles de pagos
 */
@Repository
public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
    
    /**
     * Buscar detalle de pago por ID de pago
     */
    Optional<PaymentDetail> findByPaymentId(Long paymentId);
    
    /**
     * Verificar si existe un detalle para un pago
     */
    boolean existsByPaymentId(Long paymentId);
    
    /**
     * Buscar por ID de transacción del procesador
     */
    Optional<PaymentDetail> findByProcessorTransactionId(String processorTransactionId);
} 