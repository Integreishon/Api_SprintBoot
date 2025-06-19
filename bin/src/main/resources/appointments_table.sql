-- ============================================================================
-- TABLA 9: appointments (Citas Médicas) - VERSIÓN SIMPLIFICADA
-- ============================================================================
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    specialty_id BIGINT NOT NULL REFERENCES specialties(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    reason TEXT NOT NULL,
    price DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'NO_SHOW')),
    follow_up_appointment_id BIGINT,
    cancellation_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar consultas
CREATE INDEX idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor_id ON appointments(doctor_id);
CREATE INDEX idx_appointments_specialty_id ON appointments(specialty_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_doctor_date ON appointments(doctor_id, appointment_date);
CREATE INDEX idx_appointments_doctor_date_time ON appointments(doctor_id, appointment_date, start_time);

-- Constraint para evitar citas duplicadas del mismo doctor a la misma hora
CREATE UNIQUE INDEX idx_appointments_doctor_datetime_active 
ON appointments(doctor_id, appointment_date, start_time) 
WHERE status IN ('SCHEDULED', 'CONFIRMED');
