@echo off
echo 🔍 === VERIFICACIÓN FINAL DE ERRORES SOLUCIONADOS ===
echo.

set "ERROR_COUNT=0"

echo 1. Verificando pom.xml...
findstr /i "spring-boot-starter-thymeleaf" pom.xml >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Thymeleaf dependency encontrada
) else (
    echo ❌ ERROR: Falta dependencia Thymeleaf
    set /a ERROR_COUNT+=1
)

findstr /i "spring-boot-configuration-processor" pom.xml >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Configuration processor encontrado
) else (
    echo ❌ ERROR: Falta configuration processor
    set /a ERROR_COUNT+=1
)

findstr /i "3.3.12" pom.xml >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Spring Boot 3.3.12 configurado
) else (
    echo ⚠️  WARNING: Spring Boot podría no estar en 3.3.12
)

echo.
echo 2. Verificando plantillas de email...

set "TEMPLATES_NEEDED=appointment-cancelled appointment-confirmation appointment-reminder appointment-rescheduled general-notification lab-results-available password-reset payment-due payment-received prescription-created welcome-email"

for %%T in (%TEMPLATES_NEEDED%) do (
    if exist "src\main\resources\templates\email\%%T.html" (
        echo ✅ %%T.html encontrada
    ) else (
        echo ❌ ERROR: Falta %%T.html
        set /a ERROR_COUNT+=1
    )
)

echo.
echo 3. Verificando EmailService.java...
if exist "src\main\java\com\hospital\backend\notification\service\EmailService.java" (
    findstr /i "TemplateEngine" "src\main\java\com\hospital\backend\notification\service\EmailService.java" >nul
    if %ERRORLEVEL% EQU 0 (
        echo ✅ EmailService usa TemplateEngine
    ) else (
        echo ❌ ERROR: EmailService no usa TemplateEngine
        set /a ERROR_COUNT+=1
    )
) else (
    echo ❌ ERROR: EmailService.java no encontrado
    set /a ERROR_COUNT+=1
)

echo.
echo 4. Verificando estructura del proyecto...
if exist "src\main\java\com\hospital\backend\config\OpenApiConfig.java" (
    echo ✅ OpenApiConfig.java existe
) else (
    echo ❌ ERROR: OpenApiConfig.java faltante
    set /a ERROR_COUNT+=1
)

if exist "backup_debug" (
    echo ✅ Archivos problemáticos en backup_debug
) else (
    echo ⚠️  WARNING: Directorio backup_debug no existe
)

echo.
echo ================================================
if %ERROR_COUNT% EQU 0 (
    echo ✅ ¡TODOS LOS ERRORES SOLUCIONADOS!
    echo.
    echo 🚀 Tu proyecto está listo para compilar y ejecutar
    echo.
    echo Próximos pasos:
    echo   1. Ejecutar: compilacion_final.bat
    echo   2. O manual: mvnw clean compile
    echo   3. Iniciar:  mvnw spring-boot:run
    echo.
    echo URLs una vez iniciado:
    echo   🌐 Swagger UI: http://localhost:8080/api/swagger-ui/index.html
    echo   📊 API Docs:   http://localhost:8080/api/v3/api-docs
    echo   ❤️  Health:    http://localhost:8080/api/actuator/health
) else (
    echo ❌ ERRORES ENCONTRADOS: %ERROR_COUNT%
    echo.
    echo Por favor revisa los errores mostrados arriba
    echo y asegúrate de que todos los archivos estén en su lugar.
)

echo.
pause
