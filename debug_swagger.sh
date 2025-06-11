#!/bin/bash

echo "🔍 === DEBUGGING SWAGGER ERROR 500 ==="
echo ""
echo "1. REINICIANDO APLICACIÓN CON LOGS DETALLADOS..."
echo ""

# Matar proceso Spring Boot si está corriendo
pkill -f "spring-boot:run"
sleep 2

echo "2. INICIANDO CON DEBUGGING COMPLETO..."
echo ""

# Cambiar al directorio del proyecto
cd "C:\Users\Admin\Documents\GitHub\Api_SprintBoot"

# Limpiar y compilar
echo "🧹 Limpiando proyecto..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ ERROR EN COMPILACIÓN!"
    exit 1
fi

echo "✅ Compilación exitosa"
echo ""

echo "🚀 INICIANDO APLICACIÓN (presiona Ctrl+C para detener)..."
echo "📊 Los logs se mostrarán a continuación:"
echo ""
echo "🎯 BUSCAR EN LOS LOGS:"
echo "   - 'ERROR' para errores específicos"
echo "   - 'NullPointerException' para referencias nulas"
echo "   - 'Bean' para problemas de inyección"
echo "   - 'OpenAPI' para problemas de documentación"
echo ""
echo "=================================================="

# Ejecutar con logs detallados
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dlogging.level.org.springdoc=DEBUG -Dlogging.level.io.swagger=DEBUG"
