-- ============================================================================
-- DATOS INICIALES PARA SISTEMA HOSPITALARIO - VERSIÓN UROVITAL OPTIMIZADA
-- Compatible con entidades Spring Boot JPA según nueva lógica
-- ============================================================================

-- ============================================================================
-- ESPECIALIDADES MÉDICAS (specialties) - CONFIGURACIÓN UROVITAL
-- ============================================================================
INSERT INTO specialties (name, description, consultation_price, discount_percentage, average_duration, is_active, is_primary, requires_referral, created_at, updated_at)
VALUES 
    ('Urología', 'Especialidad principal del Centro Médico Urovital', 120.00, 0.00, 30, true, true, false, NOW(), NOW()),
    ('Medicina Interna', 'Especialidad bajo demanda por derivación', 100.00, 0.00, 30, true, false, true, NOW(), NOW()),
    ('Ginecología', 'Especialidad bajo demanda por derivación', 110.00, 0.00, 30, true, false, true, NOW(), NOW()),
    ('Gastroenterología', 'Especialidad bajo demanda por derivación', 130.00, 0.00, 30, true, false, true, NOW(), NOW()),
    ('Nefrología', 'Especialidad bajo demanda por derivación', 140.00, 0.00, 30, true, false, true, NOW(), NOW()),
    ('Laboratorio', 'Servicios de análisis clínicos', 80.00, 0.00, 20, true, false, false, NOW(), NOW());

-- ============================================================================
-- MÉTODOS DE PAGO (payment_methods) - MÉTODOS LOCALES PERUANOS
-- ============================================================================
INSERT INTO payment_methods (name, type, processing_fee, integration_code, is_active, requires_manual_validation, is_digital, created_at, updated_at)
VALUES 
    ('Efectivo', 'CASH', 0.00, NULL, true, true, false, NOW(), NOW()),
    ('Yape', 'DIGITAL', 0.00, 'YAPE', true, true, true, NOW(), NOW()),
    ('Plin', 'DIGITAL', 0.00, 'PLIN', true, true, true, NOW(), NOW()),
    ('Tarjeta Crédito/Débito', 'CARD', 3.50, 'CARD', true, false, true, NOW(), NOW());

-- ============================================================================
-- USUARIOS DEL SISTEMA (users) - ROLES ACTUALIZADOS
-- ============================================================================
INSERT INTO users (email, password_hash, role, is_active, last_login, created_at, updated_at)
VALUES 
    ('admin@urovital.pe', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewFjZPiACmUNJnH6', 'ADMIN', true, NULL, NOW(), NOW()),
    ('doctor.mario@urovital.pe', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'DOCTOR', true, NULL, NOW(), NOW()),
    ('dra.mayra@urovital.pe', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'SPECIALIST', true, NULL, NOW(), NOW()),
    ('recepcion@urovital.pe', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'RECEPTIONIST', true, NULL, NOW(), NOW()),
    ('paciente@ejemplo.com', '$2a$12$qrQcKBpUkrb7Q8w6mUzyne4f1ZO5k8VehJUEC8aBUvdgfkSQKNHu.', 'PATIENT', true, NULL, NOW(), NOW());

-- ============================================================================
-- CONFIGURACIONES DEL HOSPITAL (hospital_settings)
-- ============================================================================
INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_by, updated_by, created_at, updated_at)
VALUES 
    ('hospital.name', 'Centro Médico Urovital E.I.R.L.', 'STRING', 'Nombre del centro médico', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('hospital.address', 'Av. Principal 123, Lima, Perú', 'STRING', 'Dirección del centro médico', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('hospital.phone', '(01) 234-5678', 'STRING', 'Teléfono principal', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('hospital.email', 'contacto@urovital.pe', 'STRING', 'Email de contacto', 'GENERAL', true, true, 'system', 'system', NOW(), NOW()),
    ('appointment.morning.start', '07:00', 'STRING', 'Hora inicio bloque mañana', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('appointment.morning.end', '13:00', 'STRING', 'Hora fin bloque mañana', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('appointment.afternoon.start', '16:00', 'STRING', 'Hora inicio bloque tarde', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('appointment.afternoon.end', '20:00', 'STRING', 'Hora fin bloque tarde', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('appointment.morning.capacity', '20', 'INTEGER', 'Capacidad bloque mañana', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('appointment.afternoon.capacity', '25', 'INTEGER', 'Capacidad bloque tarde', 'APPOINTMENT', false, true, 'system', 'system', NOW(), NOW()),
    ('chatbot.enabled', 'true', 'BOOLEAN', 'Si el chatbot está activo', 'SYSTEM', false, true, 'system', 'system', NOW(), NOW()),
    ('system.maintenance_mode', 'false', 'BOOLEAN', 'Modo de mantenimiento del sistema', 'SYSTEM', false, true, 'system', 'system', NOW(), NOW());

-- ============================================================================
-- BASE DE CONOCIMIENTOS DEL CHATBOT (chatbot_knowledge_base)
-- ============================================================================
INSERT INTO chatbot_knowledge_base (topic, question, answer, keywords, is_active, priority, category, usage_count, success_rate, created_by, updated_by, created_at, updated_at)
VALUES 
    ('Citas Médicas', '¿Cómo agendar una cita médica?', 'Para agendar una cita médica en Centro Médico Urovital puedes llamar al (01) 234-5678, usar nuestra plataforma web, o acercarte directamente al centro. Necesitarás tu DNI y se atenderá por orden de llegada en el bloque horario seleccionado.', 'cita,agendar,reservar,appointment', true, 5, 'PROCEDIMIENTOS', 0, 1.0, 'system', 'system', NOW(), NOW()),
    
    ('Documentación', '¿Qué documentos necesito para atenderme?', 'Para atenderte en Centro Médico Urovital solo necesitas tu DNI. Si es tu primera visita, te registraremos en nuestro sistema.', 'documentos,dni,carnet,requisitos', true, 5, 'REQUISITOS', 0, 1.0, 'system', 'system', NOW(), NOW()),
    
    ('Horarios', '¿Cuáles son los horarios de atención?', 'Nuestros horarios de atención son en dos bloques: Mañana de 7:00 AM a 1:00 PM, y Tarde de 4:00 PM a 8:00 PM. Las citas se atienden por orden de llegada dentro de cada bloque.', 'horarios,atención,bloques', true, 5, 'INFORMACIÓN', 0, 1.0, 'system', 'system', NOW(), NOW()),
    
    ('Especialidades', '¿Qué especialidades médicas tienen disponibles?', 'Nuestra especialidad principal es Urología con el Dr. Mario. También ofrecemos por derivación: Medicina Interna, Ginecología, Gastroenterología y Nefrología.', 'especialidades,doctores,médicos', true, 5, 'SERVICIOS', 0, 1.0, 'system', 'system', NOW(), NOW()),
    
    ('Pagos', '¿Cómo puedo pagar mi consulta?', 'Aceptamos efectivo, Yape, Plin y tarjetas de crédito/débito. El pago debe realizarse antes de la cita.', 'pagar,pago,tarjeta,efectivo,yape,plin', true, 5, 'FINANCIERO', 0, 1.0, 'system', 'system', NOW(), NOW());

-- ============================================================================
-- DATOS DE PRUEBA PARA DESARROLLO
-- ============================================================================

-- Paciente de prueba
INSERT INTO patients (
    user_id, document_number, first_name, last_name, second_last_name,
    birth_date, gender, phone, address, reniec_verified, created_at, updated_at
)
VALUES (
    5, -- id del usuario paciente
    '87654321',
    'Carlos',
    'López',
    'Mendoza',
    '1990-05-20',
    'MALE',
    '912345678',
    'Av. Lima 123, Lima',
    true,
    NOW(),
    NOW()
);

-- Dr. Mario (Urólogo principal)
INSERT INTO doctors (
    user_id, cmp_number, first_name, last_name, second_last_name, 
    phone, consultation_room, is_active, hire_date, profile_image,
    doctor_type, is_external, can_refer, contact_phone, created_at, updated_at
)
VALUES (
    2, -- id del usuario doctor
    'CMP123456',
    'Mario',
    'García',
    'López',
    '998877665',
    '101',
    true,
    '2020-01-15',
    NULL,
    'PRIMARY',
    false,
    true,
    '998877665',
    NOW(),
    NOW()
);

-- Dra. Mayra (Especialista en Ginecología)
INSERT INTO doctors (
    user_id, cmp_number, first_name, last_name, second_last_name, 
    phone, consultation_room, is_active, hire_date, profile_image,
    doctor_type, is_external, can_refer, contact_phone, created_at, updated_at
)
VALUES (
    3, -- id del usuario especialista
    'CMP789012',
    'Mayra',
    'Rodríguez',
    'Sánchez',
    '987654321',
    '102',
    true,
    '2021-03-10',
    NULL,
    'SPECIALIST',
    true,
    false,
    '987654321',
    NOW(),
    NOW()
);

-- Especialidad del Dr. Mario (Urología)
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
  AND s.name = 'Urología'
  AND NOT EXISTS (SELECT 1 FROM doctor_specialties WHERE doctor_id = d.id AND specialty_id = s.id);

-- Especialidad de la Dra. Mayra (Ginecología)
INSERT INTO doctor_specialties (doctor_id, specialty_id, is_primary, certification_date, created_at, updated_at)
SELECT 
    d.id,
    s.id,
    true,
    '2020-05-15',
    NOW(),
    NOW()
FROM doctors d, specialties s 
WHERE d.cmp_number = 'CMP789012' 
  AND s.name = 'Ginecología'
  AND NOT EXISTS (SELECT 1 FROM doctor_specialties WHERE doctor_id = d.id AND specialty_id = s.id);

-- Disponibilidad del Dr. Mario
INSERT INTO doctor_availability (doctor_id, day_of_week, time_block, max_patients, is_available, created_at, updated_at)
SELECT 
    d.id,
    1, -- Lunes
    'MORNING',
    20,
    true,
    NOW(),
    NOW()
FROM doctors d
WHERE d.cmp_number = 'CMP123456';

INSERT INTO doctor_availability (doctor_id, day_of_week, time_block, max_patients, is_available, created_at, updated_at)
SELECT 
    d.id,
    1, -- Lunes
    'AFTERNOON',
    25,
    true,
    NOW(),
    NOW()
FROM doctors d
WHERE d.cmp_number = 'CMP123456';

-- Disponibilidad de la Dra. Mayra (solo bajo demanda)
INSERT INTO doctor_availability (doctor_id, day_of_week, time_block, max_patients, is_available, created_at, updated_at)
SELECT 
    d.id,
    3, -- Miércoles
    'AFTERNOON',
    15,
    true,
    NOW(),
    NOW()
FROM doctors d
WHERE d.cmp_number = 'CMP789012';
