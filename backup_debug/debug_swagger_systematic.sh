#!/bin/bash

echo "🔍 === DEBUGGING SISTEMÁTICO SWAGGER ERROR 500 ==="
echo ""
echo "🎯 ESTRATEGIA: Activar grupos OpenAPI uno por uno"
echo ""

PROJECT_DIR="C:\Users\Admin\Documents\GitHub\Api_SprintBoot"
CONFIG_FILE="$PROJECT_DIR/src/main/java/com/hospital/backend/config/OpenApiConfig.java"

cd "$PROJECT_DIR"

echo "1. 🧹 LIMPIANDO PROYECTO..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ ERROR EN COMPILACIÓN!"
    exit 1
fi

echo "✅ Compilación exitosa"
echo ""

echo "2. 🚀 INICIANDO CON SOLO GRUPO 'authentication'..."
echo ""
echo "🌐 URLs PARA PROBAR:"
echo "   - Swagger UI: http://localhost:8080/api/swagger-ui/index.html"
echo "   - Auth docs: http://localhost:8080/api/v3/api-docs/authentication"
echo ""
echo "📋 PLAN DE TESTING:"
echo "   ✅ PASO 1: Solo 'authentication' (actual)"
echo "   ⏳ PASO 2: + 'appointments'"
echo "   ⏳ PASO 3: + 'medical'"
echo "   ⏳ PASO 4: + 'administration'"
echo "   ⏳ PASO 5: + 'hospital-api' (general)"
echo ""
echo "🔍 LOGS A BUSCAR:"
echo "   - 'NullPointerException' para beans faltantes"
echo "   - 'Bean creation exception' para dependencias"
echo "   - 'OpenAPI' para problemas de documentación"
echo "   - HTTP 500 en endpoints específicos"
echo ""
echo "=================================================="
echo ""

# Iniciar aplicación
mvn spring-boot:run

echo ""
echo "📝 INSTRUCCIONES DESPUÉS DE TESTING:"
echo ""
echo "SI EL GRUPO 'authentication' FUNCIONA:"
echo "1. Descomentar appointmentApi() en OpenApiConfig.java"
echo "2. Reiniciar y probar"
echo "3. Repetir para cada grupo hasta encontrar el problemático"
echo ""
echo "SI ALGÚN GRUPO FALLA:"
echo "1. Anotar cuál grupo específico causa el error"
echo "2. Revisar los controladores de ese grupo"
echo "3. Verificar DTOs y entidades relacionadas"
echo ""
echo "ENDPOINTS PARA PROBAR MANUALMENTE:"
echo "   GET http://localhost:8080/api/v3/api-docs/authentication"
echo "   GET http://localhost:8080/api/v3/api-docs/appointments (cuando se active)"
echo "   GET http://localhost:8080/api/v3/api-docs/medical (cuando se active)"
echo "   GET http://localhost:8080/api/v3/api-docs/administration (cuando se active)"
echo ""
