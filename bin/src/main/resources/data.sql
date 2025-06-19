-- ============================================================================
-- DATOS INICIALES PARA SISTEMA HOSPITALARIO - VERSIÓN CORREGIDA V3
-- SIN ON CONFLICT - Compatible con entidades Spring Boot JPA reales del proyecto
-- ============================================================================

-- ============================================================================
-- TIPOS DE DOCUMENTO (document_types)
-- ============================================================================
INSERT INTO document_types (code, name, validation_pattern, is_active, created_at, updated_at)
VALUES 
    ('DNI', 'Documento Nacional de Identidad', '^[0-9]{8}$', true, NOW(), NOW()),
    ('CE', 'Carnet de Extranjería', '^[0-9]{9}$', true, NOW(), NOW()),
    ('PAS', 'Pasaporte', '^[A-Z0-9]{6,12}$', true, NOW(), NOW()),
    ('CED', 'Cédula de Identidad', '^[0-9]{8,12}$', true, NOW(), NOW());

-- ============================================================================
-- ESPECIALIDADES MÉDICAS (specialties)
-- ============================================================================
INSERT INTO specialties (name, description, consultation_price, discount_percentage, average_duration, is_active, created_at, updated_at)
VALUES 
    ('Medicina General', 'Atención médica integral y preventiva para adultos', 80.00, 0.00, 30, true, NOW(), NOW()),
    ('Cardiología', 'Especialidad del corazón y sistema cardiovascular', 120.00, 0.00, 45, true, NOW(), NOW()),
    ('Dermatología', 'Tratamiento de enfermedades de la piel', 100.00, 0.00, 30, true, NOW(), NOW()),
    ('Pediatría', 'Atención médica especializada para niños y adolescentes', 90.00, 0.00, 30, true, NOW(), NOW()),
    ('Ginecología', 'Salud reproductiva femenina', 110.00, 0.00, 45, true, NOW(), NOW()),
    ('Traumatología', 'Tratamiento de lesiones del sistema músculo-esquelético', 130.00, 0.00, 45, true, NOW(), NOW()),
    ('Oftalmología', 'Cuidado de los ojos y la visión', 100.00, 0.00, 30, true, NOW(), NOW()),
    ('Neurología', 'Tratamiento del sistema nervioso', 140.00, 0.00, 60, true, NOW(), NOW()),
    ('Psiquiatría', 'Salud mental y trastornos psiquiátricos', 120.00, 0.00, 60, true, NOW(), NOW()),
    ('Endocrinología', 'Tratamiento de trastornos hormonales', 110.00, 0.00, 45, true, NOW(), NOW());

-- ============================================================================
-- MÉTODOS DE PAGO (payment_methods) - ESTRUCTURA SIMPLIFICADA
-- ============================================================================
INSERT INTO payment_methods (name, type, processing_fee, is_active, created_at, updated_at)
VALUES 
    ('Efectivo', 'CASH', 0.00, true, NOW(), NOW()),
    ('Visa', 'CREDIT_CARD', 3.50, true, NOW(), NOW()),
    ('Mastercard', 'CREDIT_CARD', 3.50, true, NOW(), NOW()),
    ('Tarjeta de Débito', 'DEBIT_CARD', 2.00, true, NOW(), NOW()),
    ('Transferencia Bancaria', 'BANK_TRANSFER', 5.00, true, NOW(), NOW()),
    ('Seguro Médico', 'INSURANCE', 0.00, true, NOW(), NOW());

-- ============================================================================
-- USUARIOS DEL SISTEMA (users) - PASSWORDS CONOCIDOS
-- ============================================================================
INSERT INTO users (email, password_hash, role, is_active, created_at, updated_at)
VALUES 
    ('admin@hospital.pe', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewFjZPiACmUNJnH6', 'ADMIN', true, NOW(), NOW()),
    ('doctor@hospital.pe', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'DOCTOR', true, NOW(), NOW()),
    ('paciente@hospital.pe', '$2a$12$qrQcKBpUkrb7Q8w6mUzyne4f1ZO5k8VehJUEC8aBUvdgfkSQKNHu.', 'PATIENT', true, NOW(), NOW());

-- ============================================================================
-- CONFIGURACIONES DEL HOSPITAL (hospital_settings) - ESTRUCTURA CORREGIDA
-- ============================================================================
INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_by, updated_by, created_at, updated_at)
VALUES 
    ('hospital.name', 'Hospital San Juan', 'STRING', 'Nombre del hospital', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('hospital.address', 'Av. Brasil 123, Lima, Perú', 'STRING', 'Dirección del hospital', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('hospital.phone', '(01) 234-5678', 'STRING', 'Teléfono principal del hospital', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('hospital.email', 'contacto@hospitalsanjuan.pe', 'STRING', 'Email de contacto', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('appointment.reminder_hours', '24', 'INTEGER', 'Horas antes de enviar recordatorio de cita', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('appointment.max_per_doctor_day', '20', 'INTEGER', 'Máximo de citas por doctor por día', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('chatbot.enabled', 'true', 'BOOLEAN', 'Si el chatbot está activo', 'SYSTEM', false, true, 'system', 'system', NOW(), NOW()),
    ('system.maintenance_mode', 'false', 'BOOLEAN', 'Modo de mantenimiento del sistema', 'SYSTEM', false, true, 'system', 'system', NOW(), NOW());

-- ============================================================================
-- BASE DE CONOCIMIENTOS DEL CHATBOT (chatbot_knowledge_base) - ESTRUCTURA CORREGIDA
-- ============================================================================
INSERT INTO chatbot_knowledge_base (topic, question, answer, keywords, data_type, is_active, priority, category, subcategory, usage_count, success_rate, created_by, updated_by, created_at, updated_at)
VALUES 
    ('Citas Médicas', '¿Cómo agendar una cita médica?', 'Para agendar una cita médica puedes llamar al (01) 234-5678, usar nuestra plataforma web, o acercarte directamente al hospital. Necesitarás tu DNI y especificar la especialidad que requieres.', 'cita,agendar,reservar,appointment', 'STRING', true, 5, 'PROCEDIMIENTOS', 'AGENDAMIENTO', 0, 1.0, 'system', 'system', NOW(), NOW()),
    
    ('Documentación', '¿Qué documentos necesito para atenderme?', 'Para atenderte en nuestro hospital necesitas: DNI o documento de identidad válido, carnet de seguro (si tienes), y cualquier resultado médico previo que sea relevante para tu consulta.', 'documentos,dni,carnet,requisitos', 'STRING', true, 5, 'REQUISITOS', 'DOCUMENTOS', 0, 1.0, 'system', 'system', NOW(), NOW()),
    
    ('Horarios', '¿Cuáles son los horarios de atención?', 'Nuestros horarios de atención son de Lunes a Viernes de 8:00 AM a 8:00 PM, y Sábados de 8:00 AM a 2:00 PM. Emergencias las 24 horas.', 'horarios,atención,emergencias', 'STRING', true, 5, 'INFORMACIÓN', 'HORARIOS', 0, 1.0, 'system', 'system', NOW(), NOW()),
    
    ('Especialidades', '¿Qué especialidades médicas tienen disponibles?', 'Contamos con: Medicina General, Cardiología, Dermatología, Pediatría, Ginecología, Traumatología, Oftalmología, Neurología, Psiquiatría y Endocrinología.', 'especialidades,doctores,médicos', 'STRING', true, 5, 'SERVICIOS', 'ESPECIALIDADES', 0, 1.0, 'system', 'system', NOW(), NOW()),
    
    ('Pagos', '¿Cómo puedo pagar mi consulta?', 'Aceptamos efectivo, tarjetas de crédito y débito (Visa, Mastercard), transferencias bancarias y seguros médicos. También puedes pagar en línea a través de nuestra plataforma.', 'pagar,pago,tarjeta,efectivo,seguro', 'STRING', true, 5, 'FINANCIERO', 'METODOS_PAGO', 0, 1.0, 'system', 'system', NOW(), NOW());

-- ============================================================================
-- DATOS DE PRUEBA PARA DESARROLLO - SIMPLIFICADOS
-- ============================================================================

INSERT INTO patients (
    user_id, document_type_id, document_number, first_name, last_name,
    birth_date, gender, phone, address, created_at, updated_at
)
VALUES (
   3,                      -- id del usuario relacionado
    1,                      -- id del tipo de documento (DNI, por ejemplo)
    '87654321',
    'Carlos',
    'López',
    '1990-05-20',
    'MALE',
    '912345678',
    'Av. Lima 123, Lima',
    NOW(),
    NOW()
);

-- Doctor de prueba (insertar solo si el usuario existe)
INSERT INTO doctors (user_id, cmp_number, first_name, last_name, phone, consultation_room, is_active, hire_date, profile_image, created_at, updated_at)
SELECT 
    u.id,
    'CMP123456',
    'Dr. Miguel',
    'García López',
    '998877665',
    '205',
    true,
    '2020-01-15',
    NULL,
    NOW(),
    NOW()
FROM users u 
WHERE u.email = 'doctor@hospital.pe'
  AND NOT EXISTS (SELECT 1 FROM doctors WHERE cmp_number = 'CMP123456');

-- Especialidad del doctor (insertar solo si ambos existen)
INSERT INTO doctor_specialties (doctor_id, specialty_id, is_primary, certification_date, created_at, updated_at)
SELECT 
    d.id,
    s.id,
    true,
    '2019-12-01',
    NOW(),
    NOW()
FROM doctors d, specialties s 
WHERE d.cmp_number = 'CMP123456' 
  AND s.name = 'Cardiología'
  AND NOT EXISTS (SELECT 1 FROM doctor_specialties WHERE doctor_id = d.id AND specialty_id = s.id);
