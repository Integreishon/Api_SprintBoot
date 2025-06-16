// DTO para crear nuevo doctor
package com.hospital.backend.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateDoctorRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres")
    private String password;
    
    @NotBlank(message = "El número CMP es obligatorio")
    @Pattern(regexp = "^[0-9]{6}$", message = "El CMP debe tener exactamente 9 dígitos")
    private String cmpNumber;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String firstName;
    
    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 100, message = "El apellido paterno no puede exceder 100 caracteres")
    private String lastName;
    
    @Size(max = 100, message = "El apellido materno no puede exceder 100 caracteres")
    private String secondLastName;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^9[0-9]{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    private String phone;

    
    private String consultationRoom;
    
    @PastOrPresent(message = "La fecha de contratación no puede ser futura")
    private LocalDate hireDate;
    
    private String profileImage;
    
    @NotEmpty(message = "Debe especificar al menos una especialidad")
    private List<Long> specialtyIds;
}