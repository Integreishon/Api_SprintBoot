// DTO para respuesta de autenticación
package com.hospital.backend.auth.dto.response;

import com.hospital.backend.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private UserRole role;
    private LocalDateTime expiresAt;
    
    public AuthResponse(String token, Long userId, String email, UserRole role, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.expiresAt = expiresAt;
    }
    
}