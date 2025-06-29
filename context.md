# 🏥 Contexto y Arquitectura: Portal Virtual Urovital

Este documento describe la arquitectura y el flujo de un **portal web complementario** para el Centro Médico Urovital. El objetivo del sistema no es reemplazar el software existente del centro, sino ofrecer un canal digital moderno para que los pacientes agenden y paguen sus citas de forma virtual.

---

## 1. Modelo de Negocio: Un Canal Virtual Paralelo

El sistema funciona como una capa externa que no interfiere con las operaciones diarias del centro, que seguirán utilizando su software actual para pacientes presenciales (walk-ins).

### 1.1. Propósito Estratégico
- **Ofrecer un canal de agendamiento online** que el centro actualmente no posee.
- **Reducir la carga de trabajo en recepción**, permitiendo que los pacientes se registren y paguen antes de llegar.
- **Mejorar la imagen institucional** con una experiencia de usuario moderna y profesional.
- **Centralizar la gestión de citas virtuales** en una base de datos única y accesible.

### 1.2. El Ecosistema: Dos Sistemas Conviviendo
- **Sistema Actual (del Centro)**: Gestiona 100% de los pacientes que llegan sin cita (walk-ins).
- **Este Sistema (Portal Virtual)**: Gestiona 100% de los pacientes que agendan su cita por internet.

Ambos sistemas coexisten y se reconcilian a través de un portal de validación simple usado por el personal de recepción.

---

## 2. Arquitectura de Datos: Enfocada en el Flujo Virtual

La base de datos de 16 tablas se mantiene, pero su interpretación y uso se centran exclusivamente en el ciclo de vida de una cita virtual.

### Tablas Críticas y su Rol en el Flujo Virtual

#### `users`
- **PATIENT**: El rol principal. Los usuarios se registran, gestionan sus datos y agendan citas.
- **RECEPTIONIST**: Rol con permisos limitados. **No crea citas**. Su única función es **validar los pagos** de las citas virtuales y marcar la asistencia de los pacientes.
- **DOCTOR**: Puede consultar las citas que tiene agendadas (tanto virtuales como presenciales, aunque estas últimas no estén en este sistema).
- **ADMIN**: Gestiona el portal.

#### `appointments`
- **Descripción**: El corazón del sistema. Cada registro es una cita creada por un paciente a través del portal.
- **Lógica Clave**: Una cita se crea con `paymentStatus = PROCESSING`. No puede proceder a consulta hasta que una recepcionista la valide y su estado cambie a `COMPLETED`.

#### `payments`
- **Descripción**: El registro financiero de una cita virtual. Se crea junto al `appointment` con `status = PROCESSING`.
- **Lógica Clave**: Su estado se actualiza a `COMPLETED` solo cuando la recepcionista verifica el comprobante de pago (Yape, Plin, etc.) subido por el paciente.

#### `referrals`
- **Lógica Adaptada**: La derivación sigue siendo posible. Si un paciente atendido (que agendó virtualmente) requiere un especialista, el Dr. Mario puede crear un `referral`. El paciente usaría nuevamente el portal para agendar la cita con el especialista.

---

## 3. Flujos Operacionales del Portal Virtual

### Flujo 1: Agendamiento Virtual (Realizado por el Paciente)
1.  **Registro**: El paciente se registra en el portal. Opcionalmente, se podría integrar RENIEC para autocompletar datos con DNI.
2.  **Selección de Cita**: El paciente elige especialidad (inicialmente Urología), fecha y bloque horario (`MORNING` o `AFTERNOON`) según la disponibilidad real mostrada.
3.  **Pre-Reserva y Pago Pendiente**:
    - El sistema crea un `appointment` con `paymentStatus = PROCESSING`.
    - El sistema crea un `payment` asociado con `status = PROCESSING`.
    - Se le informa al paciente que debe realizar el pago y subir el comprobante.
4.  **Carga de Comprobante**: El paciente realiza el pago por fuera (ej. Yape) y sube una imagen del comprobante a través del portal. La cita sigue "pendiente de validación".

### Flujo 2: Validación de la Cita (Realizado por la Recepcionista)
Este es el puente entre el mundo virtual y el físico.

1.  **Notificación o Revisión**: La recepcionista accede al "Portal de Validación" de este sistema. Ve una lista de citas virtuales pendientes de confirmar.
2.  **Verificación del Pago**: Abre los detalles de una cita, ve el comprobante subido por el paciente y lo coteja con la cuenta bancaria del centro.
3.  **Confirmación en el Sistema**:
    - Si el pago es correcto, hace clic en "Validar Pago".
    - El sistema actualiza el `payment.status` a `COMPLETED`.
    - El sistema actualiza el `appointment.paymentStatus` a `COMPLETED`.
    - (Opcional) El sistema envía una notificación de confirmación al paciente.
4.  **Llegada del Paciente**: Cuando el paciente llega al centro, se identifica. La recepcionista busca su cita (ya validada) en el portal y la marca como `LLEGÓ` o `EN ESPERA`.
5.  **Atención**: El paciente entra al flujo normal de atención por orden de llegada, junto con los pacientes presenciales.

---

## 4. Lógica y Validaciones Críticas del Portal

- **Separación de Roles**: Es fundamental que la API restrinja la creación de citas solo a usuarios con rol `PATIENT`. El rol `RECEPTIONIST` solo puede modificar el estado de pago y asistencia.
- **Sincronización de Estados**: La lógica de negocio debe garantizar que al validar un `payment`, el `appointment` asociado se actualice atómicamente.
- **Consulta de Disponibilidad**: La disponibilidad mostrada al paciente debe basarse en `doctor_availability`, considerando un cupo máximo para citas virtuales por bloque para no saturar la atención.
- **Seguridad de Archivos**: La subida de comprobantes debe ser segura, validando tipos de archivo y tamaños.

## 5. Conclusión: La Nueva Regla de Oro

El propósito del sistema ha sido redefinido para maximizar el valor y minimizar la fricción con los procesos existentes del cliente. La nueva regla de oro es:

> **"ESTE SISTEMA ES EL CANAL OFICIAL PARA AGENDAMIENTOS VIRTUALES, Y NINGUNA CITA VIRTUAL ES VÁLIDA HASTA QUE SEA CONFIRMADA EN EL PORTAL DE VALIDACIÓN."**

Este enfoque asegura un desarrollo enfocado, una implementación no intrusiva y una clara propuesta de valor para el Centro Médico Urovital.