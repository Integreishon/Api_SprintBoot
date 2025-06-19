-- ============================================================================
-- SCRIPT PARA LIMPIAR BASE DE DATOS ANTES DE REINICIAR
-- ============================================================================

-- Eliminar datos en el orden correcto (respetando foreign keys)
DELETE FROM doctor_specialties;
DELETE FROM doctors;
DELETE FROM patients;
DELETE FROM chatbot_knowledge_base;
DELETE FROM hospital_settings;
DELETE FROM payment_methods;
DELETE FROM specialties;
DELETE FROM document_types;
DELETE FROM users;

-- Mensaje de confirmación
SELECT 'Base de datos limpia - Lista para nueva carga de datos' as status;
