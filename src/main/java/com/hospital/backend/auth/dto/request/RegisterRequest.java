// DTO para solicitud de registro
package com.hospital.backend.auth.dto.request;

import com.hospital.backend.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
    
    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String confirmPassword;
    
    @NotNull(message = "El rol es requerido")
    private UserRole role;
    
}