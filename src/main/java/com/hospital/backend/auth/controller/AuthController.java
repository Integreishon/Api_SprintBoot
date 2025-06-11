// Controlador REST para autenticación
package com.hospital.backend.auth.controller;

import com.hospital.backend.auth.dto.request.LoginRequest;
import com.hospital.backend.auth.dto.request.RegisterRequest;
import com.hospital.backend.auth.dto.response.AuthResponse;
import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.service.AuthService;
import com.hospital.backend.auth.service.UserService;
import com.hospital.backend.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación y autorización")
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autenticar usuario y obtener token JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }
    
    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crear nueva cuenta de usuario")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registro exitoso", response));
    }
    
    @GetMapping("/profile")
    @Operation(summary = "Obtener perfil", description = "Obtener información del usuario autenticado")
    public ResponseEntity<ApiResponse<User>> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping("/change-password")
    @Operation(summary = "Cambiar contraseña", description = "Cambiar contraseña del usuario autenticado")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        
        userService.changePassword(user.getId(), currentPassword, newPassword);
        
        return ResponseEntity.ok(ApiResponse.success("Contraseña cambiada exitosamente"));
    }
}