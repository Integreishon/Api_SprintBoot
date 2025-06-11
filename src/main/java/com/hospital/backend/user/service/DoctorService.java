package com.hospital.backend.user.service;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.repository.UserRepository;
import com.hospital.backend.catalog.entity.Specialty;
import com.hospital.backend.catalog.repository.SpecialtyRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.common.exception.ValidationException;
import com.hospital.backend.enums.UserRole;
import com.hospital.backend.user.dto.request.CreateDoctorRequest;
import com.hospital.backend.user.dto.response.DoctorResponse;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.entity.DoctorSpecialty;
import com.hospital.backend.user.repository.DoctorRepository;
import com.hospital.backend.user.repository.DoctorSpecialtyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de doctores en el sistema hospitalario
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorSpecialtyRepository doctorSpecialtyRepository;
    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public DoctorResponse findById(Long id) {
        Doctor doctor = getDoctorById(id);
        return mapToDoctorResponse(doctor);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> findAll(Pageable pageable) {
        Page<Doctor> doctors = doctorRepository.findAll(pageable);
        Page<DoctorResponse> doctorResponsePage = doctors.map(this::mapToDoctorResponse);
        return new PageResponse<>(doctorResponsePage);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> findBySpecialty(Long specialtyId, Pageable pageable) {
        Page<Doctor> doctors = doctorRepository.findBySpecialtyId(specialtyId, pageable);
        Page<DoctorResponse> doctorResponsePage = doctors.map(this::mapToDoctorResponse);
        return new PageResponse<>(doctorResponsePage);
    }
    
    public DoctorResponse create(CreateDoctorRequest request) {
        validateCreateDoctorRequest(request);
        
        // Verificar si ya existe un doctor con el mismo CMP
        if (doctorRepository.existsByCmpNumber(request.getCmpNumber())) {
            throw new BusinessException("Ya existe un doctor registrado con el número de CMP: " + request.getCmpNumber());
        }
        
        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setRole(UserRole.DOCTOR);
        user.setIsActive(true);
        user = userRepository.save(user);
        
        // Crear doctor
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSecondLastName(request.getSecondLastName());
        doctor.setCmpNumber(request.getCmpNumber());
        doctor.setPhone(request.getPhone());
        doctor.setConsultationRoom(request.getConsultationRoom());
        doctor.setHireDate(request.getHireDate() != null ? request.getHireDate() : LocalDate.now());
        doctor.setIsActive(true);
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        
        // Procesar especialidades
        if (request.getSpecialtyIds() != null && !request.getSpecialtyIds().isEmpty()) {
            List<DoctorSpecialty> specialties = new ArrayList<>();
            
            for (Long specialtyId : request.getSpecialtyIds()) {
                Specialty specialty = specialtyRepository.findById(specialtyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", specialtyId));
                
                DoctorSpecialty doctorSpecialty = new DoctorSpecialty();
                doctorSpecialty.setDoctor(savedDoctor);
                doctorSpecialty.setSpecialty(specialty);
                doctorSpecialty.setCertificationDate(LocalDate.now());
                doctorSpecialty.setIsPrimary(specialties.isEmpty()); // Primera especialidad como principal
                specialties.add(doctorSpecialty);
            }
            
            doctorSpecialtyRepository.saveAll(specialties);
            savedDoctor.setSpecialties(specialties);
        }
        
        log.info("Doctor creado con ID: {} y CMP: {}", savedDoctor.getId(), savedDoctor.getCmpNumber());
        return mapToDoctorResponse(savedDoctor);
    }
    
    public DoctorResponse update(Long id, CreateDoctorRequest request) {
        Doctor doctor = getDoctorById(id);
        
        // Actualizar campos básicos
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSecondLastName(request.getSecondLastName());
        doctor.setPhone(request.getPhone());
        doctor.setConsultationRoom(request.getConsultationRoom());
        
        // Actualizar usuario si existe
        if (doctor.getUser() != null) {
            User user = doctor.getUser();
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }
        
        // Actualizar especialidades si se proporcionaron
        if (request.getSpecialtyIds() != null && !request.getSpecialtyIds().isEmpty()) {
            // Eliminar especialidades existentes
            doctorSpecialtyRepository.deleteByDoctorId(id);
            
            List<DoctorSpecialty> newSpecialties = new ArrayList<>();
            
            for (Long specialtyId : request.getSpecialtyIds()) {
                Specialty specialty = specialtyRepository.findById(specialtyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", specialtyId));
                
                DoctorSpecialty doctorSpecialty = new DoctorSpecialty();
                doctorSpecialty.setDoctor(doctor);
                doctorSpecialty.setSpecialty(specialty);
                doctorSpecialty.setCertificationDate(LocalDate.now());
                doctorSpecialty.setIsPrimary(newSpecialties.isEmpty()); // Primera especialidad como principal
                newSpecialties.add(doctorSpecialty);
            }
            
            doctorSpecialtyRepository.saveAll(newSpecialties);
            doctor.setSpecialties(newSpecialties);
        }
        
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor actualizado con ID: {}", updatedDoctor.getId());
        return mapToDoctorResponse(updatedDoctor);
    }
    
    public void delete(Long id) {
        Doctor doctor = getDoctorById(id);
        
        // En lugar de eliminar, marcamos como inactivo si existe un usuario asociado
        if (doctor.getUser() != null) {
            var user = doctor.getUser();
            user.setIsActive(false);
            userRepository.save(user);
            log.info("Usuario del doctor marcado como inactivo, ID: {}", user.getId());
        } else {
            // Eliminar relaciones y luego el doctor
            doctorSpecialtyRepository.deleteByDoctorId(id);
            doctorRepository.delete(doctor);
            log.info("Doctor eliminado con ID: {}", id);
        }
    }
    
    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> searchDoctors(String query, Pageable pageable) {
        Page<Doctor> doctors = doctorRepository.findByNameContaining(query, pageable);
        Page<DoctorResponse> doctorResponsePage = doctors.map(this::mapToDoctorResponse);
        return new PageResponse<>(doctorResponsePage);
    }
    
    // Método de utilidad para obtener un doctor por ID
    private Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
    }
    
    // Validar solicitud de creación de doctor
    private void validateCreateDoctorRequest(CreateDoctorRequest request) {
        // Validar CMP (formato peruano típico: 6 dígitos)
        if (request.getCmpNumber() == null || !request.getCmpNumber().matches("\\d{6}")) {
            throw new ValidationException("El número CMP debe tener 6 dígitos numéricos");
        }
        
        // Validar que tenga al menos una especialidad
        if (request.getSpecialtyIds() == null || request.getSpecialtyIds().isEmpty()) {
            throw new ValidationException("Debe seleccionar al menos una especialidad");
        }
        
        // Validar fecha de contratación no futura
        if (request.getHireDate() != null && request.getHireDate().isAfter(LocalDate.now())) {
            throw new ValidationException("La fecha de contratación no puede ser futura");
        }
        
        // Validar teléfono
        if (request.getPhone() != null && (request.getPhone().length() < 7 || request.getPhone().length() > 15)) {
            throw new ValidationException("El número de teléfono debe tener entre 7 y 15 dígitos");
        }
    }
    
    // Mapear entidad a DTO de respuesta
    private DoctorResponse mapToDoctorResponse(Doctor doctor) {
        DoctorResponse response = new DoctorResponse();
        response.setId(doctor.getId());
        response.setUserId(doctor.getUser() != null ? doctor.getUser().getId() : null);
        response.setEmail(doctor.getUser() != null ? doctor.getUser().getEmail() : null);
        response.setFirstName(doctor.getFirstName());
        response.setLastName(doctor.getLastName());
        response.setSecondLastName(doctor.getSecondLastName());
        response.setFullName(doctor.getFirstName() + " " + doctor.getLastName());
        response.setCmpNumber(doctor.getCmpNumber());
        response.setPhone(doctor.getPhone());
        response.setConsultationRoom(doctor.getConsultationRoom());
        response.setIsActive(doctor.getIsActive());
        response.setHireDate(doctor.getHireDate());
        
        // Mapear especialidades
        if (doctor.getSpecialties() != null) {
            response.setSpecialties(doctor.getSpecialties().stream()
                    .map(this::mapSpecialty)
                    .collect(Collectors.toList()));
            
            // Obtener especialidad primaria
            doctor.getSpecialties().stream()
                    .filter(DoctorSpecialty::getIsPrimary)
                    .findFirst()
                    .ifPresent(primarySpecialty -> {
                        response.setPrimarySpecialtyId(primarySpecialty.getSpecialty().getId());
                        response.setPrimarySpecialtyName(primarySpecialty.getSpecialty().getName());
                    });
        }
        
        return response;
    }
    
    // Mapear especialidad de doctor
    private DoctorResponse.DoctorSpecialtyDto mapSpecialty(DoctorSpecialty doctorSpecialty) {
        DoctorResponse.DoctorSpecialtyDto dto = new DoctorResponse.DoctorSpecialtyDto();
        dto.setId(doctorSpecialty.getId());
        dto.setSpecialtyId(doctorSpecialty.getSpecialty().getId());
        dto.setSpecialtyName(doctorSpecialty.getSpecialty().getName());
        dto.setCertificationDate(doctorSpecialty.getCertificationDate());
        dto.setIsPrimary(doctorSpecialty.getIsPrimary());
        return dto;
    }
}
