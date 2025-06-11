-- Script de datos iniciales para el sistema hospitalario

-- Roles de usuario
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_DOCTOR') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_PATIENT') ON CONFLICT (name) DO NOTHING;

-- Usuario administrador por defecto (email: admin@hospital.com, password: admin123)
INSERT INTO users (email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES ('admin@hospital.com', '$2a$10$m1lL9RzZQOr7YLKBLYLCAusZQ1jYRmD5V8yFJTQsNMjLELBEJEnmC', 'Admin', 'Sistema', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Asignar rol admin al usuario administrador
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@hospital.com' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Tipos de documento
INSERT INTO document_types (code, name, active, validation_regex, created_at, updated_at)
VALUES ('DNI', 'Documento Nacional de Identidad', true, '^[0-9]{8}$', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (code) DO NOTHING;

INSERT INTO document_types (code, name, active, validation_regex, created_at, updated_at)
VALUES ('CE', 'Carnet de Extranjería', true, '^[A-Z0-9]{9,12}$', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (code) DO NOTHING;

INSERT INTO document_types (code, name, active, validation_regex, created_at, updated_at)
VALUES ('PASS', 'Pasaporte', true, '^[A-Z0-9]{6,12}$', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (code) DO NOTHING;

-- Especialidades médicas
INSERT INTO specialties (name, description, price, active, created_at, updated_at)
VALUES ('Medicina General', 'Atención médica general para adultos', 50.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (name) DO NOTHING;

INSERT INTO specialties (name, description, price, active, created_at, updated_at)
VALUES ('Pediatría', 'Atención médica especializada para niños', 60.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (name) DO NOTHING;

INSERT INTO specialties (name, description, price, active, created_at, updated_at)
VALUES ('Cardiología', 'Especialidad médica para el corazón y sistema circulatorio', 80.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (name) DO NOTHING;

INSERT INTO specialties (name, description, price, active, created_at, updated_at)
VALUES ('Dermatología', 'Especialidad médica para problemas de la piel', 70.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (name) DO NOTHING;

INSERT INTO specialties (name, description, price, active, created_at, updated_at)
VALUES ('Traumatología', 'Especialidad para lesiones óseas y musculares', 75.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (name) DO NOTHING;

-- Métodos de pago
INSERT INTO payment_methods (code, name, description, fee_percentage, active, created_at, updated_at)
VALUES ('CASH', 'Efectivo', 'Pago en efectivo en caja', 0.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (code) DO NOTHING;

INSERT INTO payment_methods (code, name, description, fee_percentage, active, created_at, updated_at)
VALUES ('CARD', 'Tarjeta de Crédito/Débito', 'Pago con tarjeta VISA o Mastercard', 3.50, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (code) DO NOTHING;

INSERT INTO payment_methods (code, name, description, fee_percentage, active, created_at, updated_at)
VALUES ('TRANSFER', 'Transferencia Bancaria', 'Transferencia desde app o banca por internet', 1.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (code) DO NOTHING;

-- Configuraciones del sistema
INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_at, updated_at, created_by, updated_by)
VALUES ('hospital.name', 'Hospital Central', 'STRING', 'Nombre del hospital', 'GENERAL', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system') ON CONFLICT (setting_key) DO NOTHING;

INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_at, updated_at, created_by, updated_by)
VALUES ('hospital.address', 'Av. Principal 123, Lima', 'STRING', 'Dirección del hospital', 'GENERAL', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system') ON CONFLICT (setting_key) DO NOTHING;

INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_at, updated_at, created_by, updated_by)
VALUES ('hospital.phone', '(01) 555-1234', 'STRING', 'Teléfono del hospital', 'GENERAL', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system') ON CONFLICT (setting_key) DO NOTHING;

INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_at, updated_at, created_by, updated_by)
VALUES ('appointment.max-per-day', '20', 'INTEGER', 'Número máximo de citas por día', 'APPOINTMENT', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system') ON CONFLICT (setting_key) DO NOTHING;

INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_at, updated_at, created_by, updated_by)
VALUES ('appointment.cancellation-hours', '24', 'INTEGER', 'Horas mínimas para cancelar una cita sin penalidad', 'APPOINTMENT', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system') ON CONFLICT (setting_key) DO NOTHING;

INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_at, updated_at, created_by, updated_by)
VALUES ('payment.enable-online-payments', 'true', 'BOOLEAN', 'Habilitar pagos en línea', 'PAYMENT', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system') ON CONFLICT (setting_key) DO NOTHING;

INSERT INTO hospital_settings (setting_key, setting_value, data_type, description, category, is_public, is_editable, created_at, updated_at, created_by, updated_by)
VALUES ('system.version', '1.0.0', 'STRING', 'Versión del sistema', 'SYSTEM', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system') ON CONFLICT (setting_key) DO NOTHING; 