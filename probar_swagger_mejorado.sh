#!/bin/bash

echo "🎨 === SWAGGER CON DISEÑO MEJORADO Y GRUPOS FUNCIONALES ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

echo -e "${BLUE}Iniciando Hospital API con Swagger mejorado...${NC}"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ ERROR: No se encuentra pom.xml. Ejecuta este script desde la raíz del proyecto.${NC}"
    exit 1
fi

echo -e "${YELLOW}1. Limpiando y compilando proyecto...${NC}"
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló la compilación${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Proyecto compilado exitosamente${NC}"
echo ""

echo -e "${YELLOW}2. Verificando mejoras aplicadas...${NC}"

# Verificar que el CSS personalizado existe
if [ -f "src/main/resources/static/swagger-style.css" ]; then
    echo -e "${GREEN}✅ CSS personalizado encontrado${NC}"
else
    echo -e "${RED}❌ CSS personalizado no encontrado${NC}"
fi

# Verificar configuración en properties
if grep -q "custom-css-url" "src/main/resources/application.properties"; then
    echo -e "${GREEN}✅ Configuración CSS en properties${NC}"
else
    echo -e "${RED}❌ Configuración CSS faltante${NC}"
fi

echo ""
echo -e "${PURPLE}🔍 CAMBIOS APLICADOS:${NC}"
echo -e "${GREEN}   ✅ Grupos corregidos para filtrar correctamente${NC}"
echo -e "${GREEN}   ✅ Descripción simplificada y profesional${NC}"
echo -e "${GREEN}   ✅ CSS personalizado para mejor diseño${NC}"
echo -e "${GREEN}   ✅ Eliminadas tablas de credenciales innecesarias${NC}"
echo ""

echo -e "${YELLOW}3. Iniciando aplicación...${NC}"
echo ""
echo -e "${BLUE}📱 URLs importantes:${NC}"
echo -e "${GREEN}   🌐 Swagger UI: http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "${GREEN}   📊 API Docs:   http://localhost:8080/api/v3/api-docs${NC}"
echo -e "${GREEN}   🎨 CSS custom:  http://localhost:8080/api/static/swagger-style.css${NC}"
echo ""
echo -e "${PURPLE}🎯 QUE ESPERAR:${NC}"
echo -e "${GREEN}   ✅ Selector de grupos funciona correctamente${NC}"
echo -e "${GREEN}   ✅ Filtrado por módulos (🔐 Autenticación, 📅 Citas, etc.)${NC}"
echo -e "${GREEN}   ✅ Diseño limpio sin tablas innecesarias${NC}"
echo -e "${GREEN}   ✅ Colores y tipografía mejorados${NC}"
echo ""
echo -e "${YELLOW}🧪 GRUPOS PARA PROBAR:${NC}"
echo -e "${GREEN}   🌐 Todas las APIs - Muestra todos los endpoints${NC}"
echo -e "${GREEN}   🔐 Autenticación - Solo endpoints de /auth/**${NC}"
echo -e "${GREEN}   📅 Citas - Solo endpoints de /appointments/**${NC}"
echo -e "${GREEN}   👥 Usuarios - Solo endpoints de /patients/** y /doctors/**${NC}"
echo -e "${GREEN}   📋 Catálogos - Solo endpoints de catálogos${NC}"
echo ""
echo -e "${YELLOW}⚠️  NOTA: Presiona Ctrl+C para detener la aplicación${NC}"
echo ""
echo "=================================================="

# Iniciar la aplicación
mvn spring-boot:run
