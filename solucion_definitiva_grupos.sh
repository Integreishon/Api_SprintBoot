#!/bin/bash

echo "🔥 === SOLUCIÓN DEFINITIVA PARA GRUPOS SWAGGER ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

echo -e "${PURPLE}🛠️  APLICANDO SOLUCIÓN DEFINITIVA...${NC}"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ ERROR: No se encuentra pom.xml. Ejecuta este script desde la raíz del proyecto.${NC}"
    exit 1
fi

echo -e "${YELLOW}1. Limpiando proyecto completamente...${NC}"
mvn clean -q

echo -e "${YELLOW}2. Compilando con nueva configuración...${NC}"
mvn compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló la compilación${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilación exitosa${NC}"
echo ""

echo -e "${PURPLE}🔧 CAMBIOS APLICADOS:${NC}"
echo -e "${GREEN}   ✅ Springdoc downgradeado a versión 2.2.0 (más estable)${NC}"
echo -e "${GREEN}   ✅ Configuración simplificada sin conflictos${NC}"
echo -e "${GREEN}   ✅ Eliminado 'paths-to-match' global que causaba problemas${NC}"
echo -e "${GREEN}   ✅ Habilitado springdoc.use-fqn=true para mejor identificación${NC}"
echo ""

echo -e "${YELLOW}3. Iniciando aplicación...${NC}"
echo ""
echo -e "${BLUE}📱 URLs importantes:${NC}"
echo -e "${GREEN}   🌐 Swagger UI: http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "${GREEN}   📊 API Docs:   http://localhost:8080/api/v3/api-docs${NC}"
echo ""
echo -e "${PURPLE}🎯 GRUPOS QUE DEBERÍAN FUNCIONAR AHORA:${NC}"
echo -e "${GREEN}   🔐 Autenticación - Solo endpoints /auth/**${NC}"
echo -e "${GREEN}   👥 Usuarios - Solo endpoints /patients/** y /doctors/**${NC}"
echo -e "${GREEN}   📅 Citas - Solo endpoints /appointments/**${NC}"
echo -e "${GREEN}   📋 Catálogos - Solo endpoints /specialties/**, /document-types/**, /payment-methods/**${NC}"
echo -e "${GREEN}   🏥 Médico - Solo endpoints /medical-records/**, /prescriptions/**, /medical-attachments/**${NC}"
echo -e "${GREEN}   💰 Pagos - Solo endpoints /payments/**${NC}"
echo -e "${GREEN}   🔔 Notificaciones - Solo endpoints /notifications/**${NC}"
echo -e "${GREEN}   🤖 Chatbot - Solo endpoints /chatbot/**${NC}"
echo -e "${GREEN}   ⚙️ Administración - Solo endpoints /admin/**, /analytics/**, /audit/**${NC}"
echo ""
echo -e "${YELLOW}🧪 CÓMO PROBAR QUE FUNCIONA:${NC}"
echo -e "${GREEN}   1. Ve a Swagger UI${NC}"
echo -e "${GREEN}   2. En 'Select a definition' elige '🔐 Autenticación'${NC}"
echo -e "${GREEN}   3. Deberías ver SOLO los endpoints de /auth/**${NC}"
echo -e "${GREEN}   4. Cambia a '📅 Citas'${NC}"
echo -e "${GREEN}   5. Deberías ver SOLO los endpoints de /appointments/**${NC}"
echo ""
echo -e "${RED}⚠️  SI AÚN NO FUNCIONA:${NC}"
echo -e "${YELLOW}   - Refresca la página (Ctrl+F5)${NC}"
echo -e "${YELLOW}   - Limpia caché del navegador${NC}"
echo -e "${YELLOW}   - Verifica que la versión de springdoc se actualizó${NC}"
echo ""
echo -e "${YELLOW}⚠️  NOTA: Presiona Ctrl+C para detener la aplicación${NC}"
echo ""
echo "=================================================="

# Iniciar la aplicación
mvn spring-boot:run
