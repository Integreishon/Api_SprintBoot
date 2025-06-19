-- ============================================================================
-- CREDENCIALES DE PRUEBA - ACTUALIZADO CON PASSWORDS CONOCIDOS
-- ============================================================================

-- Los hashes BCrypt corresponden a estas passwords:
-- admin@hospital.pe    → password: admin123
-- doctor@hospital.pe   → password: doctor123  
-- paciente@hospital.pe → password: paciente123

-- Hash generado con BCrypt strength 12:
-- admin123    → $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewFjZPiACmUNJnH6
-- doctor123   → $2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.
-- paciente123 → $2a$12$qrQcKBpUkrb7Q8w6mUzyne4f1ZO5k8VehJUEC8aBUvdgfkSQKNHu.

-- ============================================================================
-- USUARIOS ACTUALIZADOS (users) - PASSWORDS CONOCIDOS
-- ============================================================================

-- ACTUALIZAR SOLO LA PASSWORD DEL PACIENTE (los otros están bien):
UPDATE users 
SET password_hash = '$2a$12$qrQcKBpUkrb7Q8w6mUzyne4f1ZO5k8VehJUEC8aBUvdgfkSQKNHu.'
WHERE email = 'paciente@hospital.pe';

-- ============================================================================
-- TESTING CREDENTIALS PARA DESARROLLO
-- ============================================================================

/*
🔐 CREDENCIALES PARA TESTING:

1. ADMIN:
   Email: admin@hospital.pe
   Password: admin123
   Rol: ADMIN

2. DOCTOR:
   Email: doctor@hospital.pe  
   Password: doctor123
   Rol: DOCTOR

3. PACIENTE:
   Email: paciente@hospital.pe
   Password: paciente123
   Rol: PATIENT

📝 NOTAS:
- Estas son credenciales SOLO para desarrollo
- Los hashes BCrypt tienen strength 12 (muy seguro)
- Para producción, cambiar todas las passwords
*/
