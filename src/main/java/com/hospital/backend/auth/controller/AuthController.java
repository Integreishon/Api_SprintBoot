// Controlador REST para autenticación con documentación mejorada
package com.hospital.backend.auth.controller;

import com.hospital.backend.auth.dto.request.LoginRequest;
import com.hospital.backend.auth.dto.request.RegisterRequest;
import com.hospital.backend.auth.dto.response.AuthResponse;
import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.service.AuthService;
import com.hospital.backend.auth.service.UserService;
import com.hospital.backend.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "🔐 Authentication", description = "Sistema de autenticación JWT")
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    @PostMapping("/login")
    @Operation(summary = "🚪 Iniciar Sesión", description = "Autenticar usuario y obtener token JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }
    
    @PostMapping("/register")
    @Operation(summary = "📝 Registrar Nueva Cuenta", description = "Crear nueva cuenta de usuario")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario registrado exitosamente", response));
    }
    
    @GetMapping("/profile")
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "👤 Ver Perfil del Usuario", description = "Obtener información del usuario autenticado")
    public ResponseEntity<ApiResponse<User>> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping("/change-password")
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "🔒 Cambiar Contraseña", description = "Cambiar contraseña del usuario autenticado")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Parameter(description = "Contraseña actual del usuario")
            @RequestParam String currentPassword,
            @Parameter(description = "Nueva contraseña")
            @RequestParam String newPassword) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        
        userService.changePassword(user.getId(), currentPassword, newPassword);
        
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada exitosamente"));
    }

    @PostMapping("/refresh")
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "🔄 Renovar Token JWT", description = "Generar nuevo token JWT")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new com.hospital.backend.common.exception.UnauthorizedException("Token requerido para renovación");
        }
        
        User user = (User) authentication.getPrincipal();
        AuthResponse newAuthResponse = authService.refreshToken(user.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", newAuthResponse.getToken());
        response.put("type", "Bearer");
        response.put("expiresIn", 86400000);
        response.put("refreshedAt", LocalDateTime.now());
        response.put("user", Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "role", user.getRole()
        ));
        
        return ResponseEntity.ok(ApiResponse.success("Token renovado exitosamente", response));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "🚪 Cerrar Sesión", description = "Cerrar sesión del usuario")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logout exitoso"));
    }

    @GetMapping("/validate")
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "✅ Validar Token JWT", description = "Verificar estado del token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new com.hospital.backend.common.exception.UnauthorizedException("Token no válido o expirado");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            // Si el principal no es un objeto User, el token es inválido o el estado es anómalo.
             throw new com.hospital.backend.common.exception.UnauthorizedException("Token inválido o sesión corrupta");
        }
        
        // Obtener el usuario del contexto de seguridad
        User user = (User) principal;
        
        Map<String, Object> validation = new HashMap<>();
        validation.put("valid", true);
        validation.put("user", Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "role", user.getRole(),
            "isActive", user.getIsActive(),
            "lastLogin", user.getLastLogin()
        ));
        validation.put("validatedAt", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success("Token validado exitosamente", validation));
    }
    
    @GetMapping("/check")
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "🔍 Verificar Autenticación", description = "Endpoint simple para verificar si el token es válido")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new com.hospital.backend.common.exception.UnauthorizedException("No autenticado");
        }
        
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put("authenticated", true);
        authInfo.put("userId", user.getId());
        authInfo.put("email", user.getEmail());
        authInfo.put("role", user.getRole());
        authInfo.put("checkedAt", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success("Usuario autenticado", authInfo));
    }
}
