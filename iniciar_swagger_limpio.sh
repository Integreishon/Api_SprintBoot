#!/bin/bash

echo "🚀 === INICIO RÁPIDO SWAGGER LIMPIO ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Preparando aplicación Hospital API con Swagger limpio...${NC}"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ ERROR: No se encuentra pom.xml. Ejecuta este script desde la raíz del proyecto.${NC}"
    exit 1
fi

echo -e "${YELLOW}1. Limpiando proyecto anterior...${NC}"
mvn clean -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló la limpieza del proyecto${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Proyecto limpiado${NC}"
echo ""

echo -e "${YELLOW}2. Compilando proyecto...${NC}"
mvn compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló la compilación${NC}"
    echo -e "${YELLOW}Intenta revisar los errores de compilación y vuelve a intentar${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Proyecto compilado exitosamente${NC}"
echo ""

echo -e "${YELLOW}3. Verificando configuración de Swagger...${NC}"

# Verificar que los archivos problemáticos no existen
PROBLEMATIC_FILES=(
    "src/main/java/com/hospital/backend/config/SwaggerController.java"
    "src/main/resources/static/swagger-ui-custom.css"
    "src/main/resources/static/swagger-ui-custom.js"
)

PROBLEM_FOUND=false
for file in "${PROBLEMATIC_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${RED}⚠️  ADVERTENCIA: Archivo problemático encontrado: $file${NC}"
        PROBLEM_FOUND=true
    fi
done

if [ "$PROBLEM_FOUND" = true ]; then
    echo -e "${YELLOW}Se encontraron archivos problemáticos. Ejecuta 'verificar_swagger_limpio.sh' para más detalles.${NC}"
    echo ""
fi

echo -e "${GREEN}✅ Configuración verificada${NC}"
echo ""

echo -e "${YELLOW}4. Iniciando aplicación Spring Boot...${NC}"
echo ""
echo -e "${BLUE}📱 URLs importantes una vez que inicie:${NC}"
echo -e "${GREEN}   🌐 Swagger UI: http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "${GREEN}   📊 API Docs:   http://localhost:8080/api/v3/api-docs${NC}"
echo -e "${GREEN}   ❤️  Health:    http://localhost:8080/api/actuator/health${NC}"
echo ""
echo -e "${BLUE}🔑 Credenciales de prueba:${NC}"
echo -e "${GREEN}   👤 Admin:     admin@hospital.pe / admin123${NC}"
echo -e "${GREEN}   👨‍⚕️ Doctor:    doctor@hospital.pe / password${NC}"
echo -e "${GREEN}   🤒 Paciente:  paciente@hospital.pe / password${NC}"
echo ""
echo -e "${YELLOW}⚠️  IMPORTANTE:${NC}"
echo -e "${YELLOW}   - Si ves errores 404 en swagger-config, detén la aplicación y ejecuta 'verificar_swagger_limpio.sh'${NC}"
echo -e "${YELLOW}   - Si ves URLs duplicadas (swagger-ui/swagger-ui), hay configuraciones conflictivas${NC}"
echo -e "${YELLOW}   - Presiona Ctrl+C para detener la aplicación${NC}"
echo ""
echo -e "${GREEN}🚀 Iniciando aplicación...${NC}"
echo ""
echo "=================================================="

# Iniciar la aplicación
mvn spring-boot:run
