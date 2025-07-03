FROM appointment_data
ON CONFLICT (id) DO NOTHING;


-- 2. Insertar el registro médico asociado a esa cita
WITH medical_record_data AS (
    SELECT
        a.id AS appointment_id,
        a.doctor_id
    FROM appointments a
    JOIN patients p ON a.patient_id = p.id
    JOIN users u ON p.user_id = u.id
    WHERE u.email = 'paciente@ejemplo.com'
      AND a.appointment_date = '2024-08-01'
    LIMIT 1
)
INSERT INTO medical_records (
    doctor_id, appointment_id, record_date,
    chief_complaint, symptoms, diagnosis, treatment_plan, notes,
    severity, weight_kg, blood_pressure, temperature,
    created_at, updated_at, created_by, updated_by
)
SELECT
    mrd.doctor_id, mrd.appointment_id, '2024-08-01 09:30:00',
    'Dolor leve en la zona lumbar',
    'Dolor intermitente desde hace 3 días',
    'Cálculo renal pequeño (urolitiasis)',
    'Aumentar ingesta de líquidos, analgésicos si es necesario. Cita de seguimiento en 2 semanas.',
    'Paciente refiere bajo estrés laboral. Se recomienda descanso.',
    'MILD', 75.5, '120/80', 36.8,
    NOW(), NOW(), 'system', 'system'
FROM medical_record_data mrd
WHERE EXISTS (SELECT 1 FROM medical_record_data)
ON CONFLICT (appointment_id) DO NOTHING; 