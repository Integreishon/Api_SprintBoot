package com.hospital.backend.user.service;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.repository.UserRepository;
import com.hospital.backend.catalog.repository.DocumentTypeRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.common.exception.ValidationException;
import com.hospital.backend.enums.UserRole;
import com.hospital.backend.user.dto.request.CreatePatientRequest;
import com.hospital.backend.user.dto.request.UpdatePatientRequest;
import com.hospital.backend.user.dto.response.PatientResponse;
import com.hospital.backend.user.entity.Patient;
import com.hospital.backend.user.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

/**
 * Servicio para la gestión de pacientes en el sistema hospitalario
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        Patient patient = getPatientById(id);
        return mapToPatientResponse(patient);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<PatientResponse> findAll(Pageable pageable) {
        Page<Patient> patients = patientRepository.findAll(pageable);
        Page<PatientResponse> patientResponsePage = patients.map(this::mapToPatientResponse);
        return new PageResponse<>(patientResponsePage);
    }
    
    @Transactional(readOnly = true)
    public PatientResponse findByDocumentNumberAndDocumentType(String documentNumber, Long documentTypeId) {
        return patientRepository.findByDocumentTypeIdAndDocumentNumber(documentTypeId, documentNumber)
                .map(this::mapToPatientResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "documento", documentNumber));
    }
    
    public PatientResponse create(CreatePatientRequest request) {
        validateCreatePatientRequest(request);
        
        var documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de documento", "id", request.getDocumentTypeId()));
        
        // Verificar si ya existe un paciente con el mismo tipo y número de documento
        Optional<Patient> existingPatient = patientRepository.findByDocumentTypeIdAndDocumentNumber(
                request.getDocumentTypeId(),
                request.getDocumentNumber());
        
        if (existingPatient.isPresent()) {
            throw new BusinessException("Ya existe un paciente registrado con este tipo y número de documento");
        }
        
        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.PATIENT);
        user.setIsActive(true);
        user = userRepository.save(user);
        
        // Crear nuevo paciente
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setSecondLastName(request.getSecondLastName());
        patient.setDocumentNumber(request.getDocumentNumber());
        patient.setDocumentType(documentType);
        patient.setBirthDate(request.getBirthDate());
        patient.setGender(request.getGender());
        patient.setBloodType(request.getBloodType());
        patient.setAddress(request.getAddress());
        patient.setPhone(request.getPhone());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        patient.setAllergies(request.getAllergies());
        patient.setChronicConditions(request.getChronicConditions());
        
        Patient savedPatient = patientRepository.save(patient);
        log.info("Paciente creado con ID: {}", savedPatient.getId());
        
        return mapToPatientResponse(savedPatient);
    }
    
    @Transactional
    public PatientResponse update(Long id, UpdatePatientRequest request) {
        Patient patient = getPatientById(id);
        
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setSecondLastName(request.getSecondLastName());
        patient.setBirthDate(request.getBirthDate());
        patient.setGender(request.getGender());
        patient.setBloodType(request.getBloodType());
        patient.setAddress(request.getAddress());
        patient.setPhone(request.getPhone());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        patient.setAllergies(request.getAllergies());
        patient.setChronicConditions(request.getChronicConditions());
        
        Patient updatedPatient = patientRepository.save(patient);
        log.info("Paciente actualizado con ID: {}", updatedPatient.getId());
        
        return mapToPatientResponse(updatedPatient);
    }
    
    public void delete(Long id) {
        Patient patient = getPatientById(id);
        
        // En lugar de eliminar, marcamos como inactivo si existe
        if (patient.getUser() != null) {
            var user = patient.getUser();
            user.setIsActive(false);
            userRepository.save(user);
            log.info("Usuario del paciente marcado como inactivo, ID: {}", user.getId());
        } else {
            patientRepository.delete(patient);
            log.info("Paciente eliminado con ID: {}", id);
        }
    }
    
    @Transactional(readOnly = true)
    public PageResponse<PatientResponse> searchPatients(String query, Pageable pageable) {
        Page<Patient> patients = patientRepository.findByNameContaining(query, pageable);
        Page<PatientResponse> patientResponsePage = patients.map(this::mapToPatientResponse);
        return new PageResponse<>(patientResponsePage);
    }
    
    // Verificar si el usuario actual es el dueño del paciente o es ADMIN
    public void validateUserCanAccessPatient(Long patientId, Long currentUserId) {
        Patient patient = getPatientById(patientId);
        
        // Si el paciente no tiene usuario asociado o el usuario actual no es el dueño
        if (patient.getUser() == null || !patient.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permisos para acceder a este paciente");
        }
    }
    
    // Método de utilidad para obtener un paciente por ID
    private Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
    }
    
    // Método para validar la solicitud de creación de paciente
    private void validateCreatePatientRequest(CreatePatientRequest request) {
        // Validar que la fecha de nacimiento no sea futura
        if (request.getBirthDate() != null && request.getBirthDate().isAfter(LocalDate.now())) {
            throw new ValidationException("La fecha de nacimiento no puede ser futura");
        }
        
        // Validar formato de teléfono (simple validación de longitud)
        if (request.getPhone() != null && (request.getPhone().length() < 7 || request.getPhone().length() > 15)) {
            throw new ValidationException("El número de teléfono debe tener entre 7 y 15 dígitos");
        }
        
        // Validar teléfono de contacto de emergencia
        if (request.getEmergencyContactPhone() != null && 
                (request.getEmergencyContactPhone().length() < 7 || 
                 request.getEmergencyContactPhone().length() > 15)) {
            throw new ValidationException("El número de teléfono de emergencia debe tener entre 7 y 15 dígitos");
        }
    }
    
    // Mapear entidad a DTO de respuesta
    private PatientResponse mapToPatientResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setFirstName(patient.getFirstName());
        response.setLastName(patient.getLastName());
        response.setFullName(patient.getFirstName() + " " + patient.getLastName());
        response.setDocumentNumber(patient.getDocumentNumber());
        
        if (patient.getDocumentType() != null) {
            response.setDocumentType(patient.getDocumentType().getName());
        }
        
        response.setBirthDate(patient.getBirthDate());
        
        // Calcular edad si la fecha de nacimiento está disponible
        if (patient.getBirthDate() != null) {
            response.setAge(Period.between(patient.getBirthDate(), LocalDate.now()).getYears());
        }
        
        response.setGender(patient.getGender());
        response.setBloodType(patient.getBloodType());
        response.setAddress(patient.getAddress());
        response.setPhone(patient.getPhone());
        response.setEmail(patient.getUser().getEmail());
        response.setUserId(patient.getUser().getId());
        response.setEmergencyContactName(patient.getEmergencyContactName());
        response.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        response.setAllergies(patient.getAllergies());
        response.setChronicConditions(patient.getChronicConditions());
        response.setCreatedAt(patient.getCreatedAt());
        response.setUpdatedAt(patient.getUpdatedAt());
        
        return response;
    }
}
