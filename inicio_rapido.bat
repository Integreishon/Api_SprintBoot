@echo off
echo 🚀 === SOLUCIÓN RÁPIDA COMPILACIÓN ===
echo.

echo Paso 1: Limpiando proyecto...
if exist target rmdir /s /q target

echo Paso 2: Verificando Java...
java -version
echo.

echo Paso 3: Intentando compilar con configuración simple...
mvnw clean compile -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ ¡ÉXITO! Iniciando aplicación...
    echo.
    mvnw spring-boot:run
) else (
    echo.
    echo ❌ ERROR - Intentando con Maven del sistema...
    mvn clean compile -DskipTests
    if %ERRORLEVEL% EQU 0 (
        echo ✅ ¡ÉXITO con Maven del sistema!
        mvn spring-boot:run
    ) else (
        echo ❌ ERROR - Revisa la configuración de Java
        echo.
        echo Verifica:
        echo 1. java -version ^(debe mostrar Java 17^)
        echo 2. mvn -version ^(Maven instalado^)
        pause
    )
)
