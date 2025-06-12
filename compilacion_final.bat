@echo off
echo 🔧 === SOLUCIONANDO ERRORES DE COMPILACIÓN FINAL ===
echo.

echo ✅ CORRECCIONES APLICADAS:
echo   - Añadida dependencia spring-boot-starter-thymeleaf
echo   - Actualizada versión Spring Boot a 3.3.12
echo   - Añadido spring-boot-configuration-processor
echo   - Creadas todas las plantillas de email faltantes
echo.

echo 🧹 Limpiando proyecto...
if exist target rmdir /s /q target

echo 🔄 Refrescando dependencias...
mvnw dependency:resolve

echo 📦 Compilando proyecto...
mvnw clean compile -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ ¡COMPILACIÓN EXITOSA!
    echo.
    echo 🚀 Iniciando aplicación...
    echo.
    echo 📋 URLs importantes:
    echo   🌐 Swagger UI: http://localhost:8080/api/swagger-ui/index.html
    echo   📊 API Docs:   http://localhost:8080/api/v3/api-docs
    echo   ❤️  Health:    http://localhost:8080/api/actuator/health
    echo.
    echo 🔑 Credenciales de prueba:
    echo   👤 Admin:     admin@hospital.pe / admin123
    echo   👨‍⚕️ Doctor:    doctor@hospital.pe / password
    echo   🤒 Paciente:  paciente@hospital.pe / password
    echo.
    mvnw spring-boot:run
) else (
    echo.
    echo ❌ ERROR EN COMPILACIÓN
    echo.
    echo 🔍 Posibles causas:
    echo   1. Versión de Java incorrecta ^(debe ser Java 17^)
    echo   2. JAVA_HOME mal configurado
    echo   3. Dependencias no descargadas correctamente
    echo.
    echo 🛠️  Soluciones:
    echo   1. Verificar: java -version
    echo   2. Verificar: echo %%JAVA_HOME%%
    echo   3. Ejecutar: mvnw dependency:purge-local-repository
    echo.
    pause
)
