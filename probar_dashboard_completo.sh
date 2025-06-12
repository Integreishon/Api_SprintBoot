#!/bin/bash

echo "🎨 === SWAGGER CON DASHBOARD MEJORADO Y GRUPOS FUNCIONALES ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${PURPLE}🚀 Iniciando Hospital API con Swagger Dashboard mejorado...${NC}"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ ERROR: No se encuentra pom.xml. Ejecuta este script desde la raíz del proyecto.${NC}"
    exit 1
fi

echo -e "${YELLOW}1. Compilando proyecto con nuevas mejoras...${NC}"
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló la compilación${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilación exitosa${NC}"
echo ""

echo -e "${CYAN}🎨 MEJORAS APLICADAS:${NC}"
echo ""
echo -e "${GREEN}   ✅ Grupos funcionando correctamente${NC}"
echo -e "${GREEN}   ✅ Grupo \"🌐 Todas las APIs\" agregado de vuelta${NC}"
echo -e "${GREEN}   ✅ Dashboard con estadísticas reales${NC}"
echo -e "${GREEN}   ✅ Diseño visual mejorado con gradientes${NC}"
echo -e "${GREEN}   ✅ Endpoint /admin/api-status/stats para datos reales${NC}"
echo -e "${GREEN}   ✅ CSS mejorado específicamente para el dashboard${NC}"
echo ""

echo -e "${YELLOW}2. Iniciando aplicación...${NC}"
echo ""
echo -e "${BLUE}📱 URLs importantes:${NC}"
echo -e "${GREEN}   🌐 Swagger UI: http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "${GREEN}   📊 API Docs:   http://localhost:8080/api/v3/api-docs${NC}"
echo -e "${GREEN}   🔍 Stats API:  http://localhost:8080/api/admin/api-status/stats${NC}"
echo -e "${GREEN}   💚 Health:     http://localhost:8080/api/admin/api-status/health${NC}"
echo ""
echo -e "${CYAN}🎯 FUNCIONALIDADES NUEVAS:${NC}"
echo ""
echo -e "${PURPLE}📊 DASHBOARD EN \"TODAS LAS APIS\":${NC}"
echo -e "${GREEN}   • Estadísticas en tiempo real del sistema${NC}"
echo -e "${GREEN}   • Estado de módulos operativos${NC}"
echo -e "${GREEN}   • Conteo de endpoints por categoría${NC}"
echo -e "${GREEN}   • Información de seguridad y autenticación${NC}"
echo -e "${GREEN}   • Diseño visual con gradientes y efectos${NC}"
echo ""
echo -e "${PURPLE}🔄 GRUPOS DE FILTRADO:${NC}"
echo -e "${GREEN}   🔐 Autenticación - Solo endpoints /auth/**${NC}"
echo -e "${GREEN}   👥 Usuarios - Solo endpoints /patients/** y /doctors/**${NC}"
echo -e "${GREEN}   📅 Citas - Solo endpoints /appointments/**${NC}"
echo -e "${GREEN}   📋 Catálogos - Solo endpoints /specialties/**, etc.${NC}"
echo -e "${GREEN}   🏥 Médico - Solo endpoints médicos${NC}"
echo -e "${GREEN}   💰 Pagos - Solo endpoints /payments/**${NC}"
echo -e "${GREEN}   🔔 Notificaciones - Solo endpoints /notifications/**${NC}"
echo -e "${GREEN}   🤖 Chatbot - Solo endpoints /chatbot/**${NC}"
echo -e "${GREEN}   ⚙️ Administración - Solo endpoints /admin/**, /analytics/**${NC}"
echo -e "${GREEN}   🌐 Todas las APIs - Vista completa con dashboard${NC}"
echo ""
echo -e "${CYAN}🧪 CÓMO PROBAR EL DASHBOARD:${NC}"
echo -e "${YELLOW}   1. Ve a Swagger UI${NC}"
echo -e "${YELLOW}   2. En 'Select a definition' elige '🌐 Todas las APIs'${NC}"
echo -e "${YELLOW}   3. Verás el dashboard con estadísticas visuales${NC}"
echo -e "${YELLOW}   4. Prueba cambiar entre diferentes grupos${NC}"
echo -e "${YELLOW}   5. Cada grupo filtra solo sus endpoints correspondientes${NC}"
echo ""
echo -e "${PURPLE}📊 NUEVOS ENDPOINTS DE ESTADÍSTICAS:${NC}"
echo -e "${GREEN}   • GET /admin/api-status/stats - Estadísticas detalladas${NC}"
echo -e "${GREEN}   • GET /admin/api-status/health - Estado de salud del sistema${NC}"
echo ""
echo -e "${BLUE}🎨 MEJORAS VISUALES:${NC}"
echo -e "${GREEN}   • Dashboard con fondo degradado azul-púrpura${NC}"
echo -e "${GREEN}   • Badges de estado en tiempo real${NC}"
echo -e "${GREEN}   • Tipografía mejorada y espaciado${NC}"
echo -e "${GREEN}   • Efectos hover y transiciones suaves${NC}"
echo -e "${GREEN}   • Diseño responsive para móviles${NC}"
echo ""
echo -e "${RED}⚠️  NOTA IMPORTANTE:${NC}"
echo -e "${YELLOW}   - El dashboard aparece SOLO en el grupo \"🌐 Todas las APIs\"${NC}"
echo -e "${YELLOW}   - Los otros grupos mantienen descripción simple${NC}"
echo -e "${YELLOW}   - Las estadísticas son datos reales del sistema${NC}"
echo -e "${YELLOW}   - Presiona Ctrl+C para detener la aplicación${NC}"
echo ""
echo "=================================================="

# Iniciar la aplicación
mvn spring-boot:run
