#!/bin/bash

echo "🎨 === SWAGGER MINIMALISTA CON ESTADÍSTICAS REALES ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${PURPLE}✨ Aplicando diseño minimalista con datos dinámicos...${NC}"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ ERROR: No se encuentra pom.xml. Ejecuta este script desde la raíz del proyecto.${NC}"
    exit 1
fi

echo -e "${YELLOW}1. Compilando proyecto con cambios minimalistas...${NC}"
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló la compilación${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilación exitosa${NC}"
echo ""

echo -e "${CYAN}🎨 CAMBIOS APLICADOS:${NC}"
echo ""
echo -e "${GREEN}   ✅ Descripción ultra-limpia en \"Todas las APIs\"${NC}"
echo -e "${GREEN}   ✅ Diseño minimalista sin efectos excesivos${NC}"
echo -e "${GREEN}   ✅ Widget dinámico con estadísticas REALES${NC}"
echo -e "${GREEN}   ✅ Conteo automático de endpoints desde OpenAPI${NC}"
echo -e "${GREEN}   ✅ Actualización automática cada 30 segundos${NC}"
echo -e "${GREEN}   ✅ Diseño responsive para móviles${NC}"
echo ""

echo -e "${YELLOW}2. Iniciando aplicación...${NC}"
echo ""
echo -e "${BLUE}📱 URLs importantes:${NC}"
echo -e "${GREEN}   🌐 Swagger UI: http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "${GREEN}   📊 Stats API:  http://localhost:8080/api/admin/api-status/stats${NC}"
echo -e "${GREEN}   📋 API Docs:   http://localhost:8080/api/v3/api-docs${NC}"
echo ""
echo -e "${CYAN}🎯 LO QUE VERÁS AHORA:${NC}"
echo ""
echo -e "${PURPLE}📊 EN \"🌐 TODAS LAS APIS\":${NC}"
echo -e "${GREEN}   • Descripción simple y limpia${NC}"
echo -e "${GREEN}   • Widget con estadísticas reales:${NC}"
echo -e "${GREEN}     - Número exacto de endpoints${NC}"
echo -e "${GREEN}     - Estado del sistema en tiempo real${NC}"
echo -e "${GREEN}     - Última actualización${NC}"
echo -e "${GREEN}   • Diseño minimalista gris claro${NC}"
echo -e "${GREEN}   • Sin efectos visuales excesivos${NC}"
echo ""
echo -e "${PURPLE}🔧 FUNCIONALIDAD TÉCNICA:${NC}"
echo -e "${GREEN}   • JavaScript obtiene datos de /v3/api-docs${NC}"
echo -e "${GREEN}   • Cuenta endpoints automáticamente${NC}"
echo -e "${GREEN}   • Si /admin/api-status/stats falla, usa conteo manual${NC}"
echo -e "${GREEN}   • Actualización automática sin recargar página${NC}"
echo -e "${GREEN}   • Solo aparece en el grupo \"Todas las APIs\"${NC}"
echo ""
echo -e "${CYAN}🧪 CÓMO PROBAR:${NC}"
echo -e "${YELLOW}   1. Ve a Swagger UI${NC}"
echo -e "${YELLOW}   2. Selecciona \"🌐 Todas las APIs\" en el dropdown${NC}"
echo -e "${YELLOW}   3. Verás descripción limpia + widget de stats${NC}"
echo -e "${YELLOW}   4. Cambia a otro grupo (ej: \"🔐 Autenticación\")${NC}"
echo -e "${YELLOW}   5. Verás que NO tiene el widget, solo descripción simple${NC}"
echo -e "${YELLOW}   6. Las estadísticas se actualizan cada 30 segundos${NC}"
echo ""
echo -e "${BLUE}📊 WIDGET DE ESTADÍSTICAS:${NC}"
echo -e "${GREEN}   ┌─────────────────────────────────────────┐${NC}"
echo -e "${GREEN}   │  [85]     [🟢 Operativo]    [12:34 PM]  │${NC}"
echo -e "${GREEN}   │ Endpoints    Estado        Actualizado │${NC}"
echo -e "${GREEN}   └─────────────────────────────────────────┘${NC}"
echo ""
echo -e "${RED}⚠️  NOTA:${NC}"
echo -e "${YELLOW}   - El widget obtiene datos REALES de tu proyecto${NC}"
echo -e "${YELLOW}   - Si agregas/quitas endpoints, el número cambia${NC}"
echo -e "${YELLOW}   - Diseño súper limpio y minimalista${NC}"
echo -e "${YELLOW}   - Presiona Ctrl+C para detener${NC}"
echo ""
echo "=================================================="

# Iniciar la aplicación
mvn spring-boot:run
