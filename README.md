# 🏥 Sistema de Gestión Hospitalaria

API RESTful para la gestión integral de un hospital, incluyendo citas, pacientes, doctores, historial médico, pagos y administración.

## 📋 Características

- **Gestión de Usuarios:** Pacientes, doctores y administradores
- **Agendamiento de Citas:** Programación y gestión de citas médicas
- **Historial Médico:** Registros médicos, recetas y archivos adjuntos
- **Pagos:** Procesamiento de pagos y facturación
- **Notificaciones:** Recordatorios y alertas por email
- **Chatbot:** Asistente virtual para consultas
- **Analytics:** Métricas, reportes y auditoría
- **Administración:** Configuración y dashboard administrativo

## 🛠️ Tecnologías

- **Backend:** Java 11, Spring Boot 2.7.x
- **Base de Datos:** PostgreSQL 13+
- **Seguridad:** JWT, Spring Security
- **Documentación:** OpenAPI 3 (Swagger)
- **Emails:** Spring Mail, Thymeleaf
- **Reportes:** JasperReports, Apache POI
- **Testing:** JUnit 5, Mockito

## 🚀 Instalación

### Requisitos Previos

- Java 11+
- Maven 3.6+
- PostgreSQL 13+

### Configuración de Base de Datos

```bash
# Crear base de datos en PostgreSQL
createdb db_hospital

# Usuario por defecto (si es necesario)
# Username: postgres
# Password: admin
```

### Configuración del Proyecto

```bash
# Clonar el repositorio
git clone https://github.com/tuusuario/hospital-api.git
cd hospital-api

# Instalar dependencias y compilar
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

### Variables de Entorno

El sistema utiliza las siguientes variables de entorno (opcionales):

```
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/db_hospital
JDBC_DATABASE_USERNAME=postgres
JDBC_DATABASE_PASSWORD=admin
JWT_SECRET=tuClaveSecretaAqui
EMAIL_USERNAME=tu@email.com
EMAIL_PASSWORD=tucontraseña
```

## 📊 Módulos Principales

### 🔐 Autenticación (auth)

- Registro y login de usuarios
- Gestión de tokens JWT
- Recuperación de contraseña

### 👥 Usuarios (user)

- Pacientes con historial y datos personales
- Doctores con especialidades y disponibilidad
- Administradores con permisos especiales

### 📅 Citas (appointment)

- Programación de citas médicas
- Verificación de disponibilidad
- Confirmación y recordatorios

### 🏥 Médico (medical)

- Historiales médicos por paciente
- Gestión de recetas médicas
- Archivos médicos adjuntos

### 💰 Pagos (payment)

- Procesamiento de pagos por cita
- Múltiples métodos de pago
- Facturación y reportes financieros

### 📱 Notificaciones (notification)

- Notificaciones por email
- Recordatorios de citas
- Alertas del sistema

### 🤖 Chatbot (chatbot)

- Asistente virtual para consultas
- Base de conocimientos personalizable
- Historial de conversaciones

### 📊 Analytics (analytics)

- Auditoría de acciones del sistema
- Métricas y KPIs del hospital
- Reportes personalizables

### ⚙️ Administración (admin)

- Configuraciones del sistema
- Dashboard administrativo
- KPIs del hospital

## 🔗 Endpoints API

La API está disponible en `http://localhost:8080/api`

- **Documentación Swagger**: `http://localhost:8080/api/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api/v3/api-docs`

## 📖 Documentación

La documentación completa del API está disponible a través de Swagger UI una vez que la aplicación está en ejecución.

## 🧪 Testing

```bash
# Ejecutar pruebas
mvn test

# Ejecutar pruebas de integración
mvn verify
```

## 🐳 Docker

```bash
# Construir imagen
docker build -t hospital-api .

# Ejecutar con Docker
docker run -p 8080:8080 hospital-api

# O con docker-compose
docker-compose up
```

## 📄 Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo LICENSE para más detalles.

## 👥 Autores

- **Tu Nombre** - _Desarrollador Principal_ - [TuUsuarioGitHub](https://github.com/tuusuario)

## 🙏 Agradecimientos

- A todos los contribuidores del proyecto
- A los profesores y mentores que apoyaron el desarrollo
