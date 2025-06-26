-- ============================================================================
-- ACTUALIZACIÓN DE ESQUEMA PARA TABLA USERS
-- ============================================================================

-- Verificar si la columna requires_activation ya existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name='users' AND column_name='requires_activation'
    ) THEN
        -- Agregar columna requires_activation
        ALTER TABLE users ADD COLUMN requires_activation BOOLEAN NOT NULL DEFAULT FALSE;
        
        -- Actualizar usuarios existentes
        UPDATE users SET requires_activation = FALSE WHERE requires_activation IS NULL;
    END IF;
END
$$;

-- Modificar columnas email y password_hash para permitir valores nulos (solo si están configuradas como NOT NULL)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name='users' AND column_name='email' AND is_nullable='NO'
    ) THEN
        ALTER TABLE users ALTER COLUMN email DROP NOT NULL;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name='users' AND column_name='password_hash' AND is_nullable='NO'
    ) THEN
        ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;
    END IF;
END
$$; 