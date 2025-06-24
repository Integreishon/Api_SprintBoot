-- ======================================
-- SCRIPT DE ACTUALIZACIÓN DE ESQUEMA
-- Centro Médico Urovital E.I.R.L.
-- ======================================

-- ============================================================================
-- 1. ELIMINAR TABLAS QUE YA NO SON NECESARIAS
-- ============================================================================

-- Eliminar tabla notifications
DROP TABLE IF EXISTS notifications CASCADE;

-- Eliminar tabla analytics_cache
DROP TABLE IF EXISTS analytics_cache CASCADE;

-- Eliminar tabla document_types (después de migrar datos)
-- NOTA: Primero migrar datos de pacientes para eliminar la referencia
ALTER TABLE patients DROP CONSTRAINT IF EXISTS fk_patients_document_type;
ALTER TABLE patients DROP COLUMN IF EXISTS document_type_id;
ALTER TABLE patients ADD CONSTRAINT uk_patients_document_number UNIQUE (document_number);
DROP TABLE IF EXISTS document_types CASCADE;

-- Eliminar tabla prescriptions
DROP TABLE IF EXISTS prescriptions CASCADE;

-- ============================================================================
-- 2. MODIFICAR CAMPOS EN TABLAS EXISTENTES
-- ============================================================================

-- Actualizar tabla users (eliminar campos innecesarios)
ALTER TABLE users DROP COLUMN IF EXISTS department;
ALTER TABLE users DROP COLUMN IF EXISTS is_external_doctor;

-- Actualizar tabla patients (eliminar campos innecesarios)
ALTER TABLE patients DROP COLUMN IF EXISTS chronic_conditions;
ALTER TABLE patients DROP COLUMN IF EXISTS preferred_appointment_type;
ALTER TABLE patients ADD COLUMN IF NOT EXISTS reniec_verified BOOLEAN DEFAULT FALSE;

-- Actualizar tabla doctors (agregar nuevos campos)
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS doctor_type VARCHAR(20) DEFAULT 'SPECIALIST';
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS is_external BOOLEAN DEFAULT FALSE;
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS can_refer BOOLEAN DEFAULT FALSE;
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(20);

-- Actualizar tabla specialties (agregar nuevos campos)
ALTER TABLE specialties ADD COLUMN IF NOT EXISTS is_primary BOOLEAN DEFAULT FALSE;
ALTER TABLE specialties ADD COLUMN IF NOT EXISTS requires_referral BOOLEAN DEFAULT FALSE;

-- Actualizar tabla payment_methods (agregar nuevos campos)
ALTER TABLE payment_methods ADD COLUMN IF NOT EXISTS requires_manual_validation BOOLEAN DEFAULT FALSE;
ALTER TABLE payment_methods ADD COLUMN IF NOT EXISTS is_digital BOOLEAN DEFAULT FALSE;

-- Actualizar tabla payments (agregar nuevos campos)
ALTER TABLE payments ADD COLUMN IF NOT EXISTS requires_validation BOOLEAN DEFAULT FALSE;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS validated_by_user_id BIGINT;
ALTER TABLE payments ADD CONSTRAINT IF NOT EXISTS fk_payments_validated_by_user FOREIGN KEY (validated_by_user_id) REFERENCES users(id);

-- Actualizar tabla medical_records (agregar campos para derivaciones)
ALTER TABLE medical_records ADD COLUMN IF NOT EXISTS is_referral_record BOOLEAN DEFAULT FALSE;
ALTER TABLE medical_records ADD COLUMN IF NOT EXISTS referral_notes TEXT;

-- Actualizar tabla doctor_availability (cambiar a bloques de tiempo)
ALTER TABLE doctor_availability DROP COLUMN IF EXISTS start_time;
ALTER TABLE doctor_availability DROP COLUMN IF EXISTS end_time;
ALTER TABLE doctor_availability DROP COLUMN IF EXISTS slot_duration;
ALTER TABLE doctor_availability ADD COLUMN IF NOT EXISTS time_block VARCHAR(20) NOT NULL DEFAULT 'MORNING';
ALTER TABLE doctor_availability ADD COLUMN IF NOT EXISTS max_patients INTEGER DEFAULT 20;
ALTER TABLE doctor_availability RENAME COLUMN is_active TO is_available;

-- Actualizar tabla appointments (simplificar radicalmente)
ALTER TABLE appointments DROP COLUMN IF EXISTS start_time;
ALTER TABLE appointments DROP COLUMN IF EXISTS end_time;
ALTER TABLE appointments DROP COLUMN IF EXISTS notes;
ALTER TABLE appointments DROP COLUMN IF EXISTS appointment_type;
ALTER TABLE appointments DROP COLUMN IF EXISTS referral_id;
ALTER TABLE appointments DROP COLUMN IF EXISTS follow_up_appointment_id;
ALTER TABLE appointments DROP COLUMN IF EXISTS virtual_meeting_url;
ALTER TABLE appointments DROP COLUMN IF EXISTS reminder_sent;
ALTER TABLE appointments DROP COLUMN IF EXISTS cancellation_reason;
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS time_block VARCHAR(20) NOT NULL DEFAULT 'MORNING';
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS payment_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED';

-- ============================================================================
-- 3. CREAR NUEVAS TABLAS
-- ============================================================================

-- Crear tabla referrals
CREATE TABLE IF NOT EXISTS referrals (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    referring_doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    target_specialty_id BIGINT NOT NULL REFERENCES specialties(id),
    original_appointment_id BIGINT NOT NULL REFERENCES appointments(id),
    new_appointment_id BIGINT REFERENCES appointments(id),
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    scheduled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_referrals_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_referrals_referring_doctor FOREIGN KEY (referring_doctor_id) REFERENCES doctors(id),
    CONSTRAINT fk_referrals_target_specialty FOREIGN KEY (target_specialty_id) REFERENCES specialties(id),
    CONSTRAINT fk_referrals_original_appointment FOREIGN KEY (original_appointment_id) REFERENCES appointments(id),
    CONSTRAINT fk_referrals_new_appointment FOREIGN KEY (new_appointment_id) REFERENCES appointments(id)
);

-- Crear índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_referrals_patient_id ON referrals(patient_id);
CREATE INDEX IF NOT EXISTS idx_referrals_referring_doctor_id ON referrals(referring_doctor_id);
CREATE INDEX IF NOT EXISTS idx_referrals_target_specialty_id ON referrals(target_specialty_id);
CREATE INDEX IF NOT EXISTS idx_referrals_original_appointment_id ON referrals(original_appointment_id);
CREATE INDEX IF NOT EXISTS idx_referrals_status ON referrals(status);

-- ============================================================================
-- 4. ACTUALIZAR ENUMS Y CONSTRAINTS
-- ============================================================================

-- Actualizar constraints para appointments
ALTER TABLE appointments DROP CONSTRAINT IF EXISTS chk_appointment_status;
ALTER TABLE appointments ADD CONSTRAINT chk_appointment_status 
    CHECK (status IN ('SCHEDULED', 'IN_CONSULTATION', 'COMPLETED', 'CANCELLED', 'NO_SHOW'));

ALTER TABLE appointments DROP CONSTRAINT IF EXISTS chk_payment_status;
ALTER TABLE appointments ADD CONSTRAINT chk_payment_status 
    CHECK (payment_status IN ('COMPLETED', 'REFUNDED'));

ALTER TABLE appointments DROP CONSTRAINT IF EXISTS chk_time_block;
ALTER TABLE appointments ADD CONSTRAINT chk_time_block 
    CHECK (time_block IN ('MORNING', 'AFTERNOON'));

-- Actualizar constraints para payments
ALTER TABLE payments DROP CONSTRAINT IF EXISTS chk_payment_status;
ALTER TABLE payments ADD CONSTRAINT chk_payment_status 
    CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED'));

-- Actualizar constraints para doctor_availability
ALTER TABLE doctor_availability DROP CONSTRAINT IF EXISTS chk_time_block;
ALTER TABLE doctor_availability ADD CONSTRAINT chk_time_block 
    CHECK (time_block IN ('MORNING', 'AFTERNOON', 'FULL_DAY'));

-- Actualizar constraints para referrals
ALTER TABLE referrals ADD CONSTRAINT chk_referral_status 
    CHECK (status IN ('REQUESTED', 'SCHEDULED', 'COMPLETED', 'CANCELLED'));

-- ============================================================================
-- 5. ACTUALIZAR ÍNDICES PARA OPTIMIZACIÓN
-- ============================================================================

-- Índices para búsquedas comunes
CREATE INDEX IF NOT EXISTS idx_appointments_date_block ON appointments(appointment_date, time_block);
CREATE INDEX IF NOT EXISTS idx_appointments_doctor_date ON appointments(doctor_id, appointment_date);
CREATE INDEX IF NOT EXISTS idx_appointments_specialty_date ON appointments(specialty_id, appointment_date);
CREATE INDEX IF NOT EXISTS idx_patients_document_number ON patients(document_number);
CREATE INDEX IF NOT EXISTS idx_doctors_doctor_type ON doctors(doctor_type);
CREATE INDEX IF NOT EXISTS idx_specialties_is_primary ON specialties(is_primary);
CREATE INDEX IF NOT EXISTS idx_specialties_requires_referral ON specialties(requires_referral);
CREATE INDEX IF NOT EXISTS idx_medical_records_is_referral_record ON medical_records(is_referral_record);

-- Actualizar datos iniciales
UPDATE specialties SET is_primary = TRUE, requires_referral = FALSE WHERE name = 'Urología';
UPDATE specialties SET is_primary = FALSE, requires_referral = TRUE WHERE name != 'Urología';

UPDATE payment_methods SET requires_manual_validation = TRUE WHERE name IN ('Efectivo', 'Yape', 'Plin');
UPDATE payment_methods SET is_digital = TRUE WHERE name IN ('Yape', 'Plin');

-- Actualizar el doctor principal
UPDATE doctors SET doctor_type = 'PRIMARY', can_refer = TRUE WHERE id = 1; 