# 📋 Plan de Desarrollo: Pivote a Portal Virtual (Hospital-Main)

Este documento detalla los pasos técnicos necesarios para adaptar el backend de Spring Boot (`hospital_main`) a la nueva estrategia de negocio: un portal de agendamiento 100% virtual que complementa, pero no interfiere con, el sistema presencial existente en el Centro Médico Urovital.

**Referencia Principal**: `context.md`

---

## 1. Objetivo y Alcance

El objetivo es transformar el backend actual para que sirva dos flujos principales:
1.  **API Pública para Pacientes**: Endpoints para que los pacientes se registren, inicien sesión, vean disponibilidad y agenden citas virtuales.
2.  **API Interna para Recepción**: Endpoints para que el personal de recepción valide los pagos de las citas virtuales y gestione el flujo de llegada.

**Supuesto Arquitectónico Clave**:
- El frontend del paciente (React, a futuro) consumirá la API de `hospital_main`.
- El frontend administrativo (`hospital_dashboard` en React) será modificado para consumir también los nuevos endpoints de validación de `hospital_main`, además de su API de Django (`hospital_admin`).

---

## 2. Fases de Desarrollo Técnico

### Fase 1: Revisión y Adaptación de la Capa de Datos (Entidades)

La base es sólida, pero se requieren pequeños ajustes para formalizar el flujo virtual.

- **Entidad `Payment`**:
    - **Acción**: Añadir un campo para almacenar la ruta del comprobante de pago subido por el paciente.
    - **Código Sugerido**:
      ```java
      @Column(name = "receipt_image_path", length = 500)
      private String receiptImagePath;
      ```

- **Entidad `Appointment`**:
    - **Acción**: Añadir un campo para registrar la hora de llegada real del paciente, que marcará la recepcionista. Esto es útil para métricas de tiempo de espera.
    - **Código Sugerido**:
      ```java
      @Column(name = "arrival_time")
      private LocalDateTime arrivalTime;
      ```
    - **Acción**: Añadir un campo para indicar el origen de la cita. Aunque ahora todo es virtual, es una buena práctica para el futuro.
    - **Código Sugerido**:
      ```java
      @Enumerated(EnumType.STRING)
      @Column(name = "channel")
      private AppointmentChannel channel = AppointmentChannel.VIRTUAL;
      // Crear enum AppointmentChannel { VIRTUAL, PRESENTIAL }
      ```

- **Entidad `User` / `Role`**:
    - **Acción**: Confirmar que los roles `PATIENT` y `RECEPTIONIST` están bien definidos y se asignan correctamente durante el registro. No se necesita más acción si ya existen.

- **Entidades de Catálogo (`Specialty`, `DoctorAvailability`)**:
    - **Acción**: No se requieren cambios. La lógica de precios, disponibilidad por bloques y cupos (`max_patients`) es directamente aplicable al portal virtual.

### Fase 2: Adaptación de la Lógica de Negocio (Services)

Aquí es donde se implementa el "corazón" del nuevo flujo.

- **`AuthService`**:
    - **Acción**: Asegurar que el método de registro cree usuarios exclusivamente con el rol `PATIENT`. El registro de `RECEPTIONIST` debe ser un proceso administrativo interno, no una API pública.

- **`AppointmentService`**:
    - **Acción**: Crear o modificar los siguientes métodos:
        - `createVirtualAppointment(UserDetails patient, CreateAppointmentRequest request)`: Crea la cita y el pago asociado, ambos en estado `PROCESSING`.
        - `uploadPaymentReceipt(Long appointmentId, MultipartFile file)`: Maneja la subida del archivo del comprobante, lo guarda de forma segura y actualiza el campo `receiptImagePath` en la entidad `Payment`.
        - `getPatientAppointments(UserDetails patient)`: Devuelve el historial de citas del paciente autenticado.

- **`PaymentService` o un nuevo `ReceptionService`**:
    - **Acción**: Crear un nuevo servicio (`ReceptionService` es más limpio) para encapsular la lógica del portal de validación.
        - `getAppointmentsPendingValidation()`: Devuelve una lista de citas cuyo `paymentStatus` es `PROCESSING` y tienen un `receiptImagePath`.
        - `validatePayment(Long paymentId, UserDetails receptionist)`: Cambia el estado del `Payment` y del `Appointment` a `COMPLETED`. Registra qué recepcionista hizo la validación.
        - `markPatientAsArrived(Long appointmentId)`: Actualiza el campo `arrivalTime` en la cita cuando el paciente llega físicamente al centro.

### Fase 3: Exposición de la API (Controllers)

Se deben crear o reestructurar los controladores para exponer la nueva lógica de negocio de forma segura.

- **`AuthController`**:
    - `POST /api/auth/register`: Abierto al público, pero internamente asigna rol `PATIENT`.
    - `POST /api/auth/login`: Estándar.

- **`AppointmentController` (API para Pacientes)**:
    - `POST /api/appointments`: Protegido para rol `PATIENT`. Llama a `appointmentService.createVirtualAppointment`.
    - `GET /api/appointments/me`: Protegido para rol `PATIENT`. Llama a `appointmentService.getPatientAppointments`.
    - `POST /api/appointments/{id}/receipt`: Protegido para rol `PATIENT`. Llama a `appointmentService.uploadPaymentReceipt`.

- **`ReceptionController` (API para Recepción)**:
    - `GET /api/reception/pending-validation`: Protegido para rol `RECEPTIONIST`.
    - `POST /api/reception/payments/{id}/validate`: Protegido para rol `RECEPTIONIST`.
    - `POST /api/reception/appointments/{id}/arrive`: Protegido para rol `RECEPTIONIST`.

### Fase 4: Configuración de Seguridad (Spring Security)

La seguridad es fundamental para separar los roles del paciente y la recepcionista.

- **Acción**: Configurar las reglas de autorización basadas en roles para los endpoints definidos en la Fase 3.
- **Código Sugerido (`SecurityConfig`)**:
  ```java
  .requestMatchers("/api/auth/**").permitAll()
  .requestMatchers("/api/appointments/**").hasRole("PATIENT")
  .requestMatchers("/api/reception/**").hasRole("RECEPTIONIST")
  .anyRequest().authenticated()
  ```
- **Acción**: Asegurar que el sistema de autenticación (probablemente JWT) funcione correctamente y que la información del rol del usuario esté presente en el token para una autorización efectiva.

---

## 3. Próximos Pasos

1.  **Implementar Cambios en Entidades**: Aplicar los ajustes en las clases `Payment` y `Appointment`.
2.  **Desarrollar los Services**: Crear la nueva lógica de negocio, empezando por los servicios de citas y recepción.
3.  **Crear los Controllers**: Exponer los servicios a través de los endpoints REST definidos.
4.  **Ajustar Spring Security**: Aplicar las reglas de autorización para proteger la API.
5.  **Probar**: Realizar pruebas unitarias y de integración para validar los nuevos flujos. 