-- ============================================================================
-- INSERCIÓN MANUAL DE DATOS BÁSICOS
-- Ejecutar estos comandos uno por uno en PostgreSQL o vía API
-- ============================================================================

-- 1. TIPOS DE DOCUMENTO
-- Delete existing document types to avoid conflicts
DELETE FROM document_types WHERE code IN ('DNI', 'CE', 'PAS', 'CED');

INSERT INTO document_types (code, name, validation_pattern, is_active, created_at, updated_at)
VALUES 
    ('DNI', 'Documento Nacional de Identidad', '^[0-9]{8}$', true, NOW(), NOW()),
    ('CE', 'Carnet de Extranjería', '^[0-9]{9}$', true, NOW(), NOW()),
    ('PAS', 'Pasaporte', '^[A-Z0-9]{6,12}$', true, NOW(), NOW()),
    ('CED', 'Cédula de Identidad', '^[0-9]{8,12}$', true, NOW(), NOW())
ON CONFLICT (code) DO NOTHING;

-- 2. ESPECIALIDADES MÉDICAS
INSERT INTO specialties (name, description, consultation_price, discount_percentage, average_duration, is_active, created_at, updated_at)
VALUES 
    ('Medicina General', 'Atención médica integral y preventiva', 80.00, 0.00, 30, true, NOW(), NOW()),
    ('Cardiología', 'Especialidad del corazón y sistema cardiovascular', 120.00, 0.00, 45, true, NOW(), NOW()),
    ('Dermatología', 'Tratamiento de enfermedades de la piel', 100.00, 0.00, 30, true, NOW(), NOW()),
    ('Pediatría', 'Atención médica para niños y adolescentes', 90.00, 0.00, 30, true, NOW(), NOW());

-- 3. MÉTODOS DE PAGO
INSERT INTO payment_methods (name, type, processing_fee, is_active, integration_code, created_at, updated_at)
VALUES 
    ('Efectivo', 'CASH', 0.00, true, 'CASH_001', NOW(), NOW()),
    ('Visa', 'CREDIT_CARD', 3.50, true, 'VISA_001', NOW(), NOW()),
    ('Mastercard', 'CREDIT_CARD', 3.50, true, 'MC_001', NOW(), NOW());

-- 4. USUARIOS DEL SISTEMA
-- Passwords: admin123, doctor123, paciente123 (hasheados con BCrypt)
-- Delete existing users to avoid conflicts
DELETE FROM users WHERE email IN ('admin@hospital.pe', 'doctor@hospital.pe', 'paciente@hospital.pe');

INSERT INTO users (email, password_hash, role, is_active, created_at, updated_at)
VALUES 
    ('admin@hospital.pe', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewFjZPiACmUNJnH6', 'ADMIN', true, NOW(), NOW()),
    ('doctor@hospital.pe', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'DOCTOR', true, NOW(), NOW()),
    ('paciente@hospital.pe', '$2a$12$6Tf3cB6Qw5UGjwIl.hJm6Ok3cF7CZF8qCNMkVjlmljVYLqFM2QNXm', 'PATIENT', true, NOW(), NOW());
