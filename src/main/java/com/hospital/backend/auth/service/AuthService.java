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
import com.hospital.backend.security.JwtTokenProvider;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndIsActive(request.getEmail(), true)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales inválidas");
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
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);
        
        user = userRepository.save(user);
        
        String token = jwtTokenProvider.generateToken(user);
        LocalDateTime expiresAt = DateUtils.nowInLima().plusSeconds(jwtTokenProvider.getExpirationTime() / 1000);
        
        log.info("Nuevo usuario registrado: {} con rol {}", user.getEmail(), user.getRole());
        
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