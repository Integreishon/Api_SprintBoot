# Nuevo Flujo del Sistema Hospital API

## Arquitectura General

El sistema ahora funciona con dos APIs independientes que comparten la misma base de datos:

1. **Spring Boot API (Puerto 8080)**
   - API principal para usuarios finales
   - Crea y mantiene el esquema de la base de datos
   - Maneja todas las operaciones principales del negocio
   - Responsable del procesamiento de datos críticos

2. **Django API (Puerto 8000)**
   - Panel administrativo y herramientas de gestión
   - Se adapta al esquema de base de datos creado por Spring Boot
   - Proporciona análisis, reportes y funciones administrativas avanzadas
   - No modifica el esquema de la base de datos

## Flujo de Datos

```
[Usuarios Finales] <----> [Spring Boot API] <----> [Base de Datos PostgreSQL] <----> [Django API] <----> [Administradores]
```

## Responsabilidades Específicas

### Spring Boot API
- Registro y autenticación de usuarios
- Gestión de citas médicas
- Historiales médicos
- Pagos y facturación
- Notificaciones a usuarios
- Lógica de negocio principal

### Django API
- Panel de administración avanzado
- Reportes y análisis de datos
- Configuración del sistema
- Auditoría y seguimiento
- Herramientas de soporte técnico
- Gestión de catálogos maestros

## Flujo de Trabajo

1. **Spring Boot** inicia primero y crea/actualiza el esquema de la base de datos
2. **Django** se inicia después y se adapta al esquema existente
3. Ambas APIs operan de forma independiente pero sobre los mismos datos
4. No hay comunicación directa entre las APIs, solo a través de la base de datos

## Consideraciones Importantes

- Las modificaciones al esquema de la base de datos SOLO deben hacerse desde Spring Boot
- Django debe adaptarse a los cambios de esquema, no iniciarlos
- Se deben evitar conflictos de bloqueo mediante transacciones adecuadas
- Cada API debe respetar su ámbito de responsabilidad
