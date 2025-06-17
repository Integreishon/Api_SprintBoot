-- ============================================================================
-- DATOS DE PRUEBA PARA APPOINTMENTS SIMPLIFICADOS
-- ============================================================================

-- Insertar algunas citas de ejemplo (asegúrate de que existan los pacientes, doctores y especialidades)
INSERT INTO appointments (patient_id, specialty_id, doctor_id, appointment_date, start_time, reason, price, status) VALUES
-- Citas para hoy y próximos días
(1, 1, 1, CURRENT_DATE + 1, '09:00:00', 'Consulta de rutina - chequeo general', 150.00, 'SCHEDULED'),
(2, 2, 2, CURRENT_DATE + 1, '10:30:00', 'Dolor en el pecho y dificultad para respirar', 200.00, 'CONFIRMED'),
(3, 3, 3, CURRENT_DATE + 2, '14:00:00', 'Revisión de análisis de sangre', 120.00, 'SCHEDULED'),
(1, 1, 1, CURRENT_DATE + 3, '11:00:00', 'Seguimiento de tratamiento', 150.00, 'SCHEDULED'),

-- Citas del pasado (para historial)
(2, 2, 2, CURRENT_DATE - 7, '09:30:00', 'Consulta inicial por dolor de cabeza', 200.00, 'COMPLETED'),
(3, 1, 1, CURRENT_DATE - 5, '15:00:00', 'Chequeo anual de salud', 150.00, 'COMPLETED'),
(1, 3, 3, CURRENT_DATE - 3, '10:00:00', 'Análisis de resultados de exámenes', 120.00, 'COMPLETED'),

-- Citas canceladas
(2, 1, 1, CURRENT_DATE - 1, '16:00:00', 'Consulta general', 150.00, 'CANCELLED'),
(3, 2, 2, CURRENT_DATE, '08:00:00', 'Revisión médica', 200.00, 'NO_SHOW');

-- Actualizar los timestamps
UPDATE appointments SET 
    created_at = CURRENT_TIMESTAMP - INTERVAL '1 hour',
    updated_at = CURRENT_TIMESTAMP - INTERVAL '30 minutes'
WHERE id IN (1, 2, 3);

UPDATE appointments SET 
    created_at = CURRENT_TIMESTAMP - INTERVAL '7 days',
    updated_at = CURRENT_TIMESTAMP - INTERVAL '7 days'
WHERE appointment_date < CURRENT_DATE;
