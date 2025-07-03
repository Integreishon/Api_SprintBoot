// Servicio de autenticación para login y registro
package com.hospital.backend.auth.service;

import com.hospital.backend.auth.dto.request.LoginRequest;
import com.hospital.backend.auth.dto.request.RegisterRequest;
import com.hospital.backend.auth.dto.response.AuthResponse;
import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.repository.UserRepository;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.UnauthorizedException;
import com.hospital.backend.common.exception.ValidationException;
import com.hospital.backend.common.util.DateUtils;
import com.hospital.backend.enums.Gender;
import com.hospital.backend.security.JwtTokenProvider;
import com.hospital.backend.user.entity.Patient;
import com.hospital.backend.user.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String dni = request.getDni();
        User user = userRepository.findByDniAndIsActive(dni, true)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas o usuario inactivo."));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales inválidas.");
        }
        
        user.setLastLogin(DateUtils.nowInLima());
        userRepository.save(user);
        
        String token = jwtTokenProvider.generateToken(user);
        LocalDateTime expiresAt = DateUtils.nowInLima().plusSeconds(jwtTokenProvider.getExpirationTime() / 1000);
        
        log.info("Usuario {} ha iniciado sesión", user.getEmail());
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole(), expiresAt);
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);
        
        if (userRepository.existsByDniOrEmail(request.getDni(), request.getEmail())) {
            throw new BusinessException("El DNI o email ya está registrado");
        }
        
        // 1. Crear y guardar el usuario
        User user = new User();
        user.setDni(request.getDni());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);
        User savedUser = userRepository.save(user);
        
        // 2. Crear y guardar el paciente asociado
        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setSecondLastName(request.getSecondLastName());
        patient.setBirthDate(request.getBirthDate());
        patient.setGender(Gender.fromString(request.getGender()));
        patient.setPhone(request.getPhone());
        patientRepository.save(patient);
        
        // 3. Generar token y respuesta
        String token = jwtTokenProvider.generateToken(savedUser);
        LocalDateTime expiresAt = DateUtils.nowInLima().plusSeconds(jwtTokenProvider.getExpirationTime() / 1000);
        
        log.info("Nuevo usuario y paciente registrado: {} con rol {}", savedUser.getEmail(), savedUser.getRole());
        
        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getRole(), expiresAt);
    }
    
    @Transactional
    public AuthResponse refreshToken(String email) {
        User user = userRepository.findByEmailAndIsActive(email, true)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
        
        String token = jwtTokenProvider.generateToken(user);
        LocalDateTime expiresAt = DateUtils.nowInLima().plusSeconds(jwtTokenProvider.getExpirationTime() / 1000);
        
        log.info("Token renovado para usuario {}", user.getEmail());
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole(), expiresAt);
    }
    
    private void validateRegisterRequest(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Las contraseñas no coinciden");
        }
        
        if (request.getPassword().length() < 6) {
            throw new ValidationException("La contraseña debe tener al menos 6 caracteres");
        }
    }
}