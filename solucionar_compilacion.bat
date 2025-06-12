@echo off
echo 🔧 === SOLUCIONANDO PROBLEMAS DE COMPILACIÓN ===
echo.

echo 1. Verificando versión de Java...
java -version
echo.

echo 2. Verificando JAVA_HOME...
echo JAVA_HOME: %JAVA_HOME%
echo.

echo 3. Limpiando proyecto completamente...
if exist target rmdir /s /q target
if exist .mvn rmdir /s /q .mvn
if exist mvnw del mvnw
if exist mvnw.cmd del mvnw.cmd

echo.
echo 4. Regenerando Maven Wrapper...
mvn wrapper:wrapper -Dmaven=3.9.9

echo.
echo 5. Limpiando con Maven Wrapper...
mvnw.cmd clean

echo.
echo 6. Compilando con Java 17...
mvnw.cmd compile -DskipTests ^
  -Dmaven.compiler.source=17 ^
  -Dmaven.compiler.target=17 ^
  -Dmaven.compiler.release=17

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ ¡COMPILACIÓN EXITOSA!
    echo.
    echo Ahora puedes iniciar la aplicación:
    echo mvnw.cmd spring-boot:run
) else (
    echo.
    echo ❌ ERROR EN COMPILACIÓN
    echo.
    echo Verifica:
    echo 1. Java version ^(debe ser 17^)
    echo 2. JAVA_HOME apunta a Java 17
    echo 3. Maven instalado correctamente
)

pause
