-- ============================================================================
-- DATOS INICIALES PARA SISTEMA HOSPITALARIO - VERSIÓN CORREGIDA
-- Compatible con entidades Spring Boot JPA con @CreatedDate/@LastModifiedDate
-- ============================================================================

-- ============================================================================
-- TIPOS DE DOCUMENTO (document_types)
-- ============================================================================
INSERT INTO document_types (code, name, validation_pattern, is_active, created_at, updated_at)
VALUES 
    ('DNI', 'Documento Nacional de Identidad', '^[0-9]{8}$', true, NOW(), NOW()),
    ('CE', 'Carnet de Extranjería', '^[0-9]{9}$', true, NOW(), NOW()),
    ('PAS', 'Pasaporte', '^[A-Z0-9]{6,12}$', true, NOW(), NOW()),
    ('CED', 'Cédula de Identidad', '^[0-9]{8,12}$', true, NOW(), NOW())
ON CONFLICT (code) DO NOTHING;

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
    ('Endocrinología', 'Tratamiento de trastornos hormonales', 110.00, 0.00, 45, true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- ============================================================================
-- MÉTODOS DE PAGO (payment_methods)
-- ============================================================================
INSERT INTO payment_methods (name, type, processing_fee, is_active, integration_code, created_at, updated_at)
VALUES 
    ('Efectivo', 'CASH', 0.00, true, 'CASH_001', NOW(), NOW()),
    ('Visa', 'CREDIT_CARD', 3.50, true, 'VISA_001', NOW(), NOW()),
    ('Mastercard', 'CREDIT_CARD', 3.50, true, 'MC_001', NOW(), NOW()),
    ('Tarjeta de Débito', 'DEBIT_CARD', 2.00, true, 'DEBIT_001', NOW(), NOW()),
    ('Transferencia Bancaria', 'BANK_TRANSFER', 5.00, true, 'TRANSFER_001', NOW(), NOW()),
    ('Seguro Médico', 'INSURANCE', 0.00, true, 'INSURANCE_001', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- ============================================================================
-- USUARIOS DEL SISTEMA (users) - Passwords hasheados con BCrypt
-- ============================================================================
INSERT INTO users (email, password_hash, role, is_active, created_at, updated_at)
VALUES 
    ('admin@hospital.pe', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewFjZPiACmUNJnH6', 'ADMIN', true, NOW(), NOW()),
    ('doctor@hospital.pe', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'DOCTOR', true, NOW(), NOW()),
    ('paciente@hospital.pe', '$2a$12$6Tf3cB6Qw5UGjwIl.hJm6Ok3cF7CZF8qCNMkVjlmljVYLqFM2QNXm', 'PATIENT', true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- CONFIGURACIONES DEL HOSPITAL (hospital_settings)
-- ============================================================================
INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_by, updated_by, created_at, updated_at)
VALUES 
    ('hospital.name', 'Hospital San Juan', 'STRING', 'Nombre del hospital', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('hospital.address', 'Av. Brasil 123, Lima, Perú', 'STRING', 'Dirección del hospital', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('hospital.phone', '(01) 234-5678', 'STRING', 'Teléfono principal del hospital', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('hospital.email', 'contacto@hospitalsanjuan.pe', 'STRING', 'Email de contacto', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('appointment.reminder_hours', '24', 'INTEGER', 'Horas antes de enviar recordatorio de cita', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('appointment.max_per_doctor_day', '20', 'INTEGER', 'Máximo de citas por doctor por día', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('appointment.cancellation_hours', '24', 'INTEGER', 'Horas mínimas para cancelar sin penalidad', 'APPOINTMENT', true, true, 'system', 'system', NOW(), NOW()),
    ('chatbot.enabled', 'true', 'BOOLEAN', 'Si el chatbot está activo', 'SYSTEM', false, true, 'system', 'system', NOW(), NOW()),
    ('system.maintenance_mode', 'false', 'BOOLEAN', 'Modo de mantenimiento del sistema', 'SYSTEM', false, true, 'system', 'system', NOW(), NOW()),
    ('payment.online_enabled', 'true', 'BOOLEAN', 'Habilitar pagos en línea', 'PAYMENT', false, true, 'system', 'system', NOW(), NOW())
ON CONFLICT (setting_key) DO NOTHING;

-- ============================================================================
-- BASE DE CONOCIMIENTOS DEL CHATBOT (chatbot_knowledge_base)
-- ============================================================================
INSERT INTO chatbot_knowledge_base (question, answer, topic, category, subcategory, keywords, priority, usage_count, success_rate, is_active, data_type, created_by, updated_by, created_at, updated_at)
VALUES 
    ('¿Cómo agendar una cita médica?', 'Para agendar una cita médica puedes llamar al (01) 234-5678, usar nuestra plataforma web, o acercarte directamente al hospital. Necesitarás tu DNI y especificar la especialidad que requieres.', 'Citas Médicas', 'PROCEDIMIENTOS', 'AGENDAMIENTO', 'cita,agendar,reservar,appointment', 1, 0, 1.0, true, 'STRING', 'system', 'system', NOW(), NOW()),
    
    ('¿Qué documentos necesito para atenderme?', 'Para atenderte en nuestro hospital necesitas: DNI o documento de identidad válido, carnet de seguro (si tienes), y cualquier resultado médico previo que sea relevante para tu consulta.', 'Documentación', 'REQUISITOS', 'DOCUMENTOS', 'documentos,dni,carnet,requisitos', 1, 0, 1.0, true, 'STRING', 'system', 'system', NOW(), NOW()),
    
    ('¿Cuáles son los horarios de atención?', 'Nuestros horarios de atención son de Lunes a Viernes de 8:00 AM a 8:00 PM, y Sábados de 8:00 AM a 2:00 PM. Emergencias las 24 horas.', 'Horarios', 'INFORMACIÓN', 'HORARIOS', 'horarios,atención,emergencias', 1, 0, 1.0, true, 'STRING', 'system', 'system', NOW(), NOW()),
    
    ('¿Qué especialidades médicas tienen disponibles?', 'Contamos con: Medicina General, Cardiología, Dermatología, Pediatría, Ginecología, Traumatología, Oftalmología, Neurología, Psiquiatría y Endocrinología.', 'Especialidades', 'SERVICIOS', 'ESPECIALIDADES', 'especialidades,doctores,médicos', 1, 0, 1.0, true, 'STRING', 'system', 'system', NOW(), NOW()),
    
    ('¿Cómo puedo pagar mi consulta?', 'Aceptamos efectivo, tarjetas de crédito y débito (Visa, Mastercard), transferencias bancarias y seguros médicos. También puedes pagar en línea a través de nuestra plataforma.', 'Pagos', 'FINANCIERO', 'METODOS_PAGO', 'pagar,pago,tarjeta,efectivo,seguro', 1, 0, 1.0, true, 'STRING', 'system', 'system', NOW(), NOW())
ON CONFLICT (question) DO NOTHING;

-- ============================================================================
-- DATOS DE PRUEBA PARA DESARROLLO
-- ============================================================================

-- Paciente de prueba
INSERT INTO patients (user_id, document_type_id, document_number, first_name, last_name, birth_date, gender, phone, address, created_at, updated_at)
SELECT 
    u.id, 
    dt.id,
    '12345678',
    'Juan Carlos',
    'Pérez González',
    '1985-03-15',
    'MALE',
    '987654321',
    'Jr. Los Olivos 456, San Isidro, Lima',
    NOW(),
    NOW()
FROM users u, document_types dt 
WHERE u.email = 'paciente@hospital.pe' 
  AND dt.code = 'DNI'
  AND NOT EXISTS (SELECT 1 FROM patients WHERE user_id = u.id);

-- Doctor de prueba
INSERT INTO doctors (user_id, cmp_number, first_name, last_name, phone, consultation_room, is_active, hire_date, created_at, updated_at)
SELECT 
    u.id,
    'CMP123456',
    'Dr. Miguel',
    'García López',
    '998877665',
    '205',
    true,
    '2020-01-15',
    NOW(),
    NOW()
FROM users u 
WHERE u.email = 'doctor@hospital.pe'
  AND NOT EXISTS (SELECT 1 FROM doctors WHERE cmp_number = 'CMP123456');

-- Especialidad del doctor
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
