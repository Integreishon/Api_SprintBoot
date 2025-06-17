package com.hospital.backend.payment.repository;

import com.hospital.backend.enums.PaymentMethodType;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con pagos
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // =========================
    // Consultas básicas
    // =========================
    
    /**
     * Buscar pago por ID de cita
     */
    Optional<Payment> findByAppointmentId(Long appointmentId);
    
    /**
     * Verificar si existe un pago para una cita
     */
    boolean existsByAppointmentId(Long appointmentId);
    
    /**
     * Buscar por número de recibo
     */
    Optional<Payment> findByReceiptNumber(String receiptNumber);
    
    /**
     * Buscar por referencia de transacción
     */
    Optional<Payment> findByTransactionReference(String transactionReference);
    
    // =========================
    // Consultas por estado
    // =========================
    
    /**
     * Listar pagos por estado
     */
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    
    /**
     * Listar pagos por estado ordenados por fecha
     */
    Page<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status, Pageable pageable);
    
    /**
     * Contar pagos por estado
     */
    Long countByStatus(PaymentStatus status);
    
    // =========================
    // Consultas por fechas
    // =========================
    
    /**
     * Buscar pagos por rango de fechas de pago
     */
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Buscar pagos por rango de fechas de pago con paginación
     */
    Page<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Buscar pagos por rango de fechas de creación
     */
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Buscar pagos por rango de fechas de creación con paginación
     */
    Page<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // =========================
    // Consultas por método de pago
    // =========================
    
    /**
     * Listar pagos por tipo de método de pago
     */
    @Query("SELECT p FROM Payment p JOIN p.paymentMethod pm WHERE pm.type = :type")
    Page<Payment> findByPaymentMethodType(@Param("type") PaymentMethodType type, Pageable pageable);
    
    /**
     * Contar pagos por tipo de método de pago
     */
    @Query("SELECT COUNT(p) FROM Payment p JOIN p.paymentMethod pm WHERE pm.type = :type")
    Long countByPaymentMethodType(@Param("type") PaymentMethodType type);
    
    // =========================
    // Consultas por entidades relacionadas
    // =========================
    
    /**
     * Obtener pagos por paciente
     */
    @Query("SELECT p FROM Payment p JOIN p.appointment a JOIN a.patient pat WHERE pat.id = :patientId ORDER BY p.createdAt DESC")
    Page<Payment> findByPatientId(@Param("patientId") Long patientId, Pageable pageable);
    
    /**
     * Obtener pagos por doctor
     */
    @Query("SELECT p FROM Payment p JOIN p.appointment a JOIN a.doctor d WHERE d.id = :doctorId ORDER BY p.createdAt DESC")
    Page<Payment> findByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);
    
    /**
     * Obtener pagos por especialidad
     */
    @Query("SELECT p FROM Payment p JOIN p.appointment a JOIN a.specialty s WHERE s.id = :specialtyId ORDER BY p.createdAt DESC")
    Page<Payment> findBySpecialtyId(@Param("specialtyId") Long specialtyId, Pageable pageable);
    
    // =========================
    // Estadísticas y reportes
    // =========================
    
    /**
     * Calcular ingresos totales por estado y rango de fechas
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE " +
           "p.status = :status AND " +
           "p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueByStatusAndDateRange(@Param("status") PaymentStatus status,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calcular comisiones totales por rango de fechas
     */
    @Query("SELECT COALESCE(SUM(p.processingFee), 0) FROM Payment p WHERE " +
           "p.status = 'COMPLETED' AND " +
           "p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalFeesByDateRange(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * Contar pagos por estado y rango de fechas
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE " +
           "p.status = :status AND " +
           "p.createdAt BETWEEN :startDate AND :endDate")
    Long countByStatusAndDateRange(@Param("status") PaymentStatus status,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calcular ingresos por tipo de método de pago
     */
    @Query("SELECT pm.type, COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "JOIN p.paymentMethod pm WHERE " +
           "p.status = 'COMPLETED' AND " +
           "p.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY pm.type")
    List<Object[]> calculateRevenueByPaymentMethodType(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * Obtener resumen de pagos por mes
     */
    @Query("SELECT YEAR(p.paymentDate), MONTH(p.paymentDate), COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p WHERE p.status = 'COMPLETED' " +
           "GROUP BY YEAR(p.paymentDate), MONTH(p.paymentDate) " +
           "ORDER BY YEAR(p.paymentDate) DESC, MONTH(p.paymentDate) DESC")
    List<Object[]> getMonthlyPaymentSummary();
    
    /**
     * Calcular ingresos diarios para el último mes
     */
    @Query("SELECT DATE(p.paymentDate), COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.status = 'COMPLETED' AND " +
           "p.paymentDate >= :startDate " +
           "GROUP BY DATE(p.paymentDate) " +
           "ORDER BY DATE(p.paymentDate)")
    List<Object[]> calculateDailyRevenueForLastMonth(@Param("startDate") LocalDateTime startDate);
    
    // =========================
    // Consultas de validación
    // =========================
    
    /**
     * Verificar si existe un pago completado para una cita
     */
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE " +
           "p.appointment.id = :appointmentId AND " +
           "p.status = 'COMPLETED'")
    boolean existsCompletedPaymentForAppointment(@Param("appointmentId") Long appointmentId);
    
    /**
     * Calcular ingresos totales
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal calculateTotalRevenue();
    
    /**
     * Calcular ingresos en un rango de fechas
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE " +
           "p.status = 'COMPLETED' AND " +
           "p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueForDateRange(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calcular ingresos por día para la última semana
     */
    @Query("SELECT DATE(p.paymentDate), COALESCE(SUM(p.amount), 0) FROM Payment p WHERE " +
           "p.status = 'COMPLETED' AND " +
           "p.paymentDate >= :startDate " +
           "GROUP BY DATE(p.paymentDate) " +
           "ORDER BY DATE(p.paymentDate)")
    List<Object[]> calculateRevenuePerDayForLastWeek(@Param("startDate") LocalDateTime startDate);
}
