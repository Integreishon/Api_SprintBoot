# 📋 CENTRO MÉDICO UROVITAL - NUEVA LÓGICA IMPLEMENTADA

## 🎯 **RESUMEN DE CAMBIOS**

Este proyecto ha sido optimizado específicamente para el modelo de negocio de **Centro Médico Urovital E.I.R.L.**, simplificando la estructura y adaptándola a sus necesidades reales.

### **Principales cambios:**

- **Reducción de complejidad**: De 19 a 16 tablas (-70% complejidad)
- **Bloques de tiempo**: Reemplazo de slots específicos por bloques MAÑANA/TARDE
- **Pago obligatorio**: Todas las citas requieren pago confirmado
- **Derivaciones internas**: Sistema de referrals entre especialidades
- **Especialistas bajo demanda**: Solo accesibles por derivación del urólogo principal

---

## 📊 **ESTRUCTURA DE TABLAS**

### **TABLAS ELIMINADAS (4):**
- ❌ **notifications** (comunicación manual)
- ❌ **analytics_cache** (optimización innecesaria)
- ❌ **document_types** (solo DNI necesario)
- ❌ **prescriptions** (físico + attachments)

### **TABLAS AGREGADAS (1):**
- 🆕 **referrals** (derivaciones internas)

### **TABLAS MODIFICADAS (10):**
- 🔄 **users** (nuevos roles: SPECIALIST, RECEPTIONIST)
- 🔄 **patients** (solo DNI, sin document_type_id)
- 🔄 **doctors** (nuevos campos: doctor_type, is_external, can_refer)
- 🔄 **specialties** (nuevos campos: is_primary, requires_referral)
- 🔄 **appointments** (simplificado: bloques en vez de horarios)
- 🔄 **payment_methods** (adaptado a métodos locales peruanos)
- 🔄 **payments** (validación manual para Yape/Plin)
- 🔄 **medical_records** (campos para derivaciones)
- 🔄 **doctor_availability** (bloques de tiempo en vez de slots)
- 🔄 **medical_attachments** (soporte para recetas subidas)

---

## 🔄 **NUEVOS FLUJOS DE NEGOCIO**

### **1. CITAS DIRECTAS (SOLO UROLOGÍA)**
```
1. Paciente selecciona Urología (única especialidad con is_primary=true)
2. Elige fecha y bloque (MAÑANA/TARDE)
3. Realiza pago (obligatorio para crear cita)
4. Cita creada con status=SCHEDULED y payment_status=COMPLETED
```

### **2. DERIVACIONES INTERNAS**
```
1. Dr. Mario (can_refer=true) atiende paciente
2. Decide derivar a especialista (ej: Ginecología)
3. Crea referral con status=REQUESTED
4. Recepcionista coordina con especialista
5. Crea nueva cita y actualiza referral.status=SCHEDULED
```

### **3. PAGOS Y VALIDACIONES**
```
1. Efectivo/Yape/Plin: requires_manual_validation=true
2. Recepcionista valida comprobante
3. Marca payment.status=COMPLETED
4. Cita confirmada automáticamente
```

---

## 🧩 **NUEVOS ENUMS IMPLEMENTADOS**

### **TimeBlock**
- `MORNING`: 07:00-13:00, max_patients=20
- `AFTERNOON`: 16:00-20:00, max_patients=25
- `FULL_DAY`: 07:00-20:00, max_patients=45

### **AppointmentStatus**
- `SCHEDULED`: Programada y pagada
- `IN_CONSULTATION`: En consulta
- `COMPLETED`: Completada
- `CANCELLED`: Cancelada
- `NO_SHOW`: No asistió

### **PaymentStatus**
- `PROCESSING`: En proceso de validación
- `COMPLETED`: Pago confirmado
- `FAILED`: Pago fallido
- `REFUNDED`: Reembolsado

### **ReferralStatus**
- `REQUESTED`: Solicitada por doctor
- `SCHEDULED`: Programada con especialista
- `COMPLETED`: Completada
- `CANCELLED`: Cancelada

### **DoctorType**
- `PRIMARY`: Doctor principal (Dr. Mario)
- `SPECIALIST`: Especialistas bajo demanda

### **UserRole**
- `PATIENT`: Pacientes
- `DOCTOR`: Dr. Mario
- `SPECIALIST`: Doctores bajo demanda
- `RECEPTIONIST`: Personal administrativo
- `ADMIN`: Administradores

### **PaymentMethodType**
- `CASH`: Efectivo
- `DIGITAL`: Yape/Plin
- `CARD`: Tarjetas crédito/débito

---

## 🚀 **INSTRUCCIONES DE IMPLEMENTACIÓN**

### **1. Actualizar esquema de base de datos:**
```sql
-- Ejecutar script de actualización
source schema_update.sql
```

### **2. Cargar datos iniciales:**
```sql
-- Cargar configuración inicial
source data.sql
```

### **3. Verificar configuración:**
- Urología debe tener `is_primary=true`
- Dr. Mario debe tener `can_refer=true`
- Especialidades derivables deben tener `requires_referral=true`

---

## 🔒 **VALIDACIONES DE NEGOCIO**

- ✅ Solo se crea appointment cuando payment.status = COMPLETED
- ✅ Solo doctores con can_refer=true pueden crear referrals
- ✅ Solo especialidades con requires_referral=false permiten citas directas
- ✅ Solo especialidades con requires_referral=true aceptan derivaciones
- ✅ Pagos con requires_validation=true necesitan validated_by_user_id

---

## 📱 **INTEGRACIÓN CON OTRAS APIS**

### **Django API (Admin/Backoffice)**
- Gestión de configuración y especialidades
- Dashboard de derivaciones pendientes
- Validación manual de pagos Yape/Plin
- Asignación de especialistas a derivaciones

### **React Frontend**
- Selección de bloques en vez de horarios específicos
- Validación de capacidad por bloque
- Flujo de pago obligatorio antes de crear cita

### **Kotlin App**
- Misma lógica que React Frontend
- Adaptada para dispositivos móviles

---

**Este proyecto ha sido optimizado específicamente para el modelo de negocio real de Centro Médico Urovital E.I.R.L.** 