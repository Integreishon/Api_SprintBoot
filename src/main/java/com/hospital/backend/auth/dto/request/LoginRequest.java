// DTO para solicitud de login
package com.hospital.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "El DNI es requerido")
    private String dni;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}