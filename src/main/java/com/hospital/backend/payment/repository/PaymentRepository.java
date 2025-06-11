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

    // Buscar pago por ID de cita
    Optional<Payment> findByAppointmentId(Long appointmentId);
    
    // Listar pagos por estado
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    
    // Listar pagos por método de pago
    @Query("SELECT p FROM Payment p JOIN p.paymentMethod pm WHERE pm.type = :type")
    Page<Payment> findByPaymentMethodType(@Param("type") PaymentMethodType type, Pageable pageable);
    
    // Buscar pagos por rango de fechas
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Calcular monto total de pagos completados por rango de fechas
    @Query("SELECT SUM(p.totalAmount) FROM Payment p WHERE p.status = 'PAID' AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Obtener pagos por paciente
    @Query("SELECT p FROM Payment p JOIN p.appointment a JOIN a.patient pat WHERE pat.id = :patientId")
    Page<Payment> findByPatientId(@Param("patientId") Long patientId, Pageable pageable);
    
    // Obtener pagos por doctor
    @Query("SELECT p FROM Payment p JOIN p.appointment a JOIN a.doctor d WHERE d.id = :doctorId")
    Page<Payment> findByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);
    
    // Contar pagos por estado y rango de fechas
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.paymentDate BETWEEN :startDate AND :endDate")
    Long countByStatusAndDateRange(
            @Param("status") PaymentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p")
    BigDecimal calculateTotalRevenue();
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.createdAt BETWEEN :start AND :end")
    BigDecimal calculateRevenueForDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT DATE(p.createdAt) as date, COALESCE(SUM(p.amount), 0) as amount " +
           "FROM Payment p " +
           "WHERE p.createdAt >= :startDate " +
           "GROUP BY DATE(p.createdAt) " +
           "ORDER BY date")
    List<Object[]> calculateRevenuePerDayForLastWeek();
} 