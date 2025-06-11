package com.hospital.backend.catalog.service;

import com.hospital.backend.catalog.dto.request.PaymentMethodRequest;
import com.hospital.backend.catalog.dto.response.PaymentMethodResponse;
import com.hospital.backend.catalog.entity.PaymentMethod;
import com.hospital.backend.catalog.repository.PaymentMethodRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.PaymentMethodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de métodos de pago en el sistema hospitalario
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    
    @Transactional(readOnly = true)
    public PaymentMethodResponse findById(Long id) {
        PaymentMethod paymentMethod = getPaymentMethodById(id);
        return mapToPaymentMethodResponse(paymentMethod);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<PaymentMethodResponse> findAll(Pageable pageable) {
        Page<PaymentMethod> paymentMethods = paymentMethodRepository.findAll(pageable);
        Page<PaymentMethodResponse> paymentMethodResponses = paymentMethods.map(this::mapToPaymentMethodResponse);
        return new PageResponse<>(paymentMethodResponses);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<PaymentMethodResponse> findByActiveStatus(Boolean isActive, Pageable pageable) {
        Page<PaymentMethod> paymentMethods = paymentMethodRepository.findByIsActive(isActive, pageable);
        Page<PaymentMethodResponse> paymentMethodResponses = paymentMethods.map(this::mapToPaymentMethodResponse);
        return new PageResponse<>(paymentMethodResponses);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> findAllActive() {
        return paymentMethodRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToPaymentMethodResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> findByType(PaymentMethodType type) {
        return paymentMethodRepository.findByTypeAndIsActiveTrue(type)
                .stream()
                .map(this::mapToPaymentMethodResponse)
                .collect(Collectors.toList());
    }
    
    public PaymentMethodResponse create(PaymentMethodRequest request) {
        // Validar que no exista un método de pago con el mismo nombre y tipo
        if (paymentMethodRepository.existsByNameAndType(request.getName(), request.getType())) {
            throw new BusinessException("Ya existe un método de pago con el nombre: " + 
                    request.getName() + " y tipo: " + request.getType().getDisplayName());
        }
        
        // Crear el nuevo método de pago
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName(request.getName());
        paymentMethod.setType(request.getType());
        paymentMethod.setProcessingFee(request.getProcessingFee());
        paymentMethod.setIsActive(request.getIsActive());
        paymentMethod.setIntegrationCode(request.getIntegrationCode());
        
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        log.info("Método de pago creado con ID: {} y nombre: {}", 
                savedPaymentMethod.getId(), savedPaymentMethod.getName());
        
        return mapToPaymentMethodResponse(savedPaymentMethod);
    }
    
    public PaymentMethodResponse update(Long id, PaymentMethodRequest request) {
        PaymentMethod paymentMethod = getPaymentMethodById(id);
        
        // Validar que no exista otro método de pago con el mismo nombre y tipo (diferente al actual)
        if ((!paymentMethod.getName().equals(request.getName()) || 
                !paymentMethod.getType().equals(request.getType())) &&
                paymentMethodRepository.existsByNameAndType(request.getName(), request.getType())) {
            throw new BusinessException("Ya existe un método de pago con el nombre: " + 
                    request.getName() + " y tipo: " + request.getType().getDisplayName());
        }
        
        // Actualizar el método de pago
        paymentMethod.setName(request.getName());
        paymentMethod.setType(request.getType());
        paymentMethod.setProcessingFee(request.getProcessingFee());
        paymentMethod.setIsActive(request.getIsActive());
        paymentMethod.setIntegrationCode(request.getIntegrationCode());
        
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        log.info("Método de pago actualizado con ID: {}", updatedPaymentMethod.getId());
        
        return mapToPaymentMethodResponse(updatedPaymentMethod);
    }
    
    public void delete(Long id) {
        PaymentMethod paymentMethod = getPaymentMethodById(id);
        
        // En lugar de eliminar físicamente, marcamos como inactivo
        paymentMethod.setIsActive(false);
        paymentMethodRepository.save(paymentMethod);
        log.info("Método de pago marcado como inactivo, ID: {}", id);
    }
    
    // Método de utilidad para obtener un método de pago por ID
    private PaymentMethod getPaymentMethodById(Long id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago", "id", id));
    }
    
    // Mapear entidad a DTO de respuesta
    private PaymentMethodResponse mapToPaymentMethodResponse(PaymentMethod paymentMethod) {
        PaymentMethodResponse response = new PaymentMethodResponse();
        response.setId(paymentMethod.getId());
        response.setName(paymentMethod.getName());
        response.setType(paymentMethod.getType());
        response.setTypeName(paymentMethod.getType().getDisplayName());
        response.setProcessingFee(paymentMethod.getProcessingFee());
        response.setIsActive(paymentMethod.getIsActive());
        response.setIntegrationCode(paymentMethod.getIntegrationCode());
        return response;
    }
}
