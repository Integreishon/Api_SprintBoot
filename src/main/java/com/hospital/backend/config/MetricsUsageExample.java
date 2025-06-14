package com.hospital.backend.config;

import org.springframework.context.annotation.Configuration;

/**
 * Ejemplo de cómo usar las métricas corregidas en tus servicios
 */
@Configuration
public class MetricsUsageExample {

    /**
     * Ejemplo de uso en AppointmentService - MÉTODO CORRECTO
     */
    public void exampleUsageInAppointmentService() {
        
        // Inyectar HospitalMetrics en tu servicio
        // @Autowired
        // private ActuatorConfig.HospitalMetrics hospitalMetrics;
        
        // Uso correcto del Timer:
        /*
        public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
            // 1. Iniciar el timer
            Timer.Sample sample = hospitalMetrics.startAppointmentProcessingTimer();
            
            try {
                // 2. Tu lógica de negocio aquí
                AppointmentResponse response = processAppointment(request);
                
                // 3. Incrementar contador de éxito
                hospitalMetrics.incrementAppointmentCounter();
                
                return response;
                
            } finally {
                // 4. Registrar el tiempo transcurrido (IMPORTANTE: en el finally)
                hospitalMetrics.recordAppointmentProcessingTime(sample);
            }
        }
        */
    }
    
    /**
     * Ejemplo de uso en PatientService
     */
    public void exampleUsageInPatientService() {
        /*
        public PatientResponse registerPatient(CreatePatientRequest request) {
            // Incrementar contador de pacientes registrados
            hospitalMetrics.incrementPatientRegistrationCounter();
            
            // Timer para consultas de base de datos
            Timer.Sample dbSample = hospitalMetrics.startDatabaseQueryTimer();
            
            try {
                // Operación de base de datos
                Patient savedPatient = patientRepository.save(patient);
                
                return mapToResponse(savedPatient);
                
            } finally {
                // Registrar tiempo de consulta DB
                hospitalMetrics.recordDatabaseQueryTime(dbSample);
            }
        }
        */
    }
    
    /**
     * Ejemplo de uso en AuthService
     */
    public void exampleUsageInAuthService() {
        /*
        public AuthResponse login(LoginRequest request) {
            // Incrementar contador de intentos de login
            hospitalMetrics.incrementLoginAttemptCounter();
            
            // Resto de la lógica de autenticación...
            
            return authResponse;
        }
        */
    }
}
