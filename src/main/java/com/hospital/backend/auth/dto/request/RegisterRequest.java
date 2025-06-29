// DTO para solicitud de registro
package com.hospital.backend.auth.dto.request;

import com.hospital.backend.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "El DNI es requerido")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener solo 8 dígitos numéricos")
    private String dni;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]|.*[0-9]).+$", message = "La contraseña debe tener al menos una minúscula y una mayúscula o número")
    private String password;
    
    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String confirmPassword;
    
    @NotNull(message = "El rol es requerido")
    private UserRole role;
    
}