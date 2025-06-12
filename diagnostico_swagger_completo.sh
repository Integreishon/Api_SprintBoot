#!/bin/bash

echo "🔍 === DIAGNÓSTICO COMPLETO DE SWAGGER ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

echo -e "${PURPLE}🔧 Verificando configuración de Swagger...${NC}"
echo ""

# Verificar archivos estáticos
echo -e "${YELLOW}1. Verificando archivos estáticos...${NC}"

if [ -f "src/main/resources/static/swagger-style.css" ]; then
    echo -e "${GREEN}   ✅ swagger-style.css existe${NC}"
else
    echo -e "${RED}   ❌ swagger-style.css NO encontrado${NC}"
fi

if [ -f "src/main/resources/static/swagger-stats.js" ]; then
    echo -e "${GREEN}   ✅ swagger-stats.js existe${NC}"
else
    echo -e "${RED}   ❌ swagger-stats.js NO encontrado${NC}"
fi

echo ""
echo -e "${YELLOW}2. Compilando proyecto...${NC}"
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló la compilación${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilación exitosa${NC}"
echo ""

echo -e "${YELLOW}3. Iniciando aplicación para diagnóstico...${NC}"
mvn spring-boot:run > /dev/null 2>&1 &
APP_PID=$!

echo -e "${GREEN}✅ Aplicación iniciada (PID: $APP_PID)${NC}"
echo -e "${YELLOW}⏳ Esperando 25 segundos para que inicie completamente...${NC}"

sleep 25

echo ""
echo -e "${YELLOW}4. Probando URLs de recursos estáticos...${NC}"

# Probar CSS
echo -e "${BLUE}🎨 Probando CSS personalizado...${NC}"
CSS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/static/swagger-style.css)

if [ "$CSS_CODE" = "200" ]; then
    echo -e "${GREEN}   ✅ CSS carga correctamente (HTTP: $CSS_CODE)${NC}"
    CSS_SIZE=$(curl -s http://localhost:8080/api/static/swagger-style.css | wc -c)
    echo -e "${GREEN}   📏 Tamaño del CSS: $CSS_SIZE bytes${NC}"
else
    echo -e "${RED}   ❌ CSS NO carga (HTTP: $CSS_CODE)${NC}"
fi

# Probar JavaScript
echo -e "${BLUE}📜 Probando JavaScript personalizado...${NC}"
JS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/static/swagger-stats.js)

if [ "$JS_CODE" = "200" ]; then
    echo -e "${GREEN}   ✅ JavaScript carga correctamente (HTTP: $JS_CODE)${NC}"
    JS_SIZE=$(curl -s http://localhost:8080/api/static/swagger-stats.js | wc -c)
    echo -e "${GREEN}   📏 Tamaño del JS: $JS_SIZE bytes${NC}"
else
    echo -e "${RED}   ❌ JavaScript NO carga (HTTP: $JS_CODE)${NC}"
fi

echo ""
echo -e "${YELLOW}5. Probando endpoints de API...${NC}"

# Probar Swagger UI
echo -e "${BLUE}🌐 Probando Swagger UI...${NC}"
SWAGGER_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/swagger-ui/index.html)

if [ "$SWAGGER_CODE" = "200" ]; then
    echo -e "${GREEN}   ✅ Swagger UI carga correctamente (HTTP: $SWAGGER_CODE)${NC}"
else
    echo -e "${RED}   ❌ Swagger UI NO carga (HTTP: $SWAGGER_CODE)${NC}"
fi

# Probar API docs
echo -e "${BLUE}📊 Probando API docs...${NC}"
DOCS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v3/api-docs)

if [ "$DOCS_CODE" = "200" ]; then
    echo -e "${GREEN}   ✅ API docs cargan correctamente (HTTP: $DOCS_CODE)${NC}"
    
    # Contar endpoints
    ENDPOINTS=$(curl -s http://localhost:8080/api/v3/api-docs | grep -o '"\/[^"]*"' | wc -l)
    echo -e "${GREEN}   📈 Total de endpoints detectados: $ENDPOINTS${NC}"
else
    echo -e "${RED}   ❌ API docs NO cargan (HTTP: $DOCS_CODE)${NC}"
fi

# Probar grupos específicos
echo -e "${BLUE}🔍 Probando grupos específicos...${NC}"
GROUPS=("01-all" "02-authentication" "03-users" "04-appointments")

for group in "${GROUPS[@]}"; do
    GROUP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/v3/api-docs/$group")
    
    if [ "$GROUP_CODE" = "200" ]; then
        echo -e "${GREEN}   ✅ Grupo '$group' funciona${NC}"
    else
        echo -e "${RED}   ❌ Grupo '$group' falló (HTTP: $GROUP_CODE)${NC}"
    fi
done

echo ""
echo -e "${YELLOW}6. Probando endpoint de estadísticas...${NC}"
STATS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/admin/api-status/stats)

if [ "$STATS_CODE" = "200" ]; then
    echo -e "${GREEN}   ✅ Endpoint de estadísticas funciona${NC}"
    echo -e "${BLUE}   📊 Datos de estadísticas:${NC}"
    curl -s http://localhost:8080/api/admin/api-status/stats | python3 -m json.tool 2>/dev/null || echo "   (No se pudo formatear JSON)"
else
    echo -e "${YELLOW}   ⚠️ Endpoint de estadísticas no disponible (HTTP: $STATS_CODE)${NC}"
    echo -e "${YELLOW}   (El JavaScript usará conteo manual)${NC}"
fi

echo ""
echo -e "${YELLOW}7. Deteniendo aplicación...${NC}"
kill $APP_PID
wait $APP_PID 2>/dev/null

echo -e "${GREEN}✅ Aplicación detenida${NC}"
echo ""

echo -e "${PURPLE}📋 RESUMEN DEL DIAGNÓSTICO:${NC}"
echo ""
echo -e "${BLUE}🎯 PARA PROBAR MANUALMENTE:${NC}"
echo -e "${GREEN}   1. Ejecuta: mvn spring-boot:run${NC}"
echo -e "${GREEN}   2. Ve a: http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "${GREEN}   3. Abre Dev Tools (F12) → Console${NC}"
echo -e "${GREEN}   4. Busca mensajes que empiecen con 🔧 🚀 ✅${NC}"
echo -e "${GREEN}   5. Selecciona \"🌐 Todas las APIs\" en el dropdown${NC}"
echo -e "${GREEN}   6. Deberías ver un widget con 3 números debajo de la descripción${NC}"
echo ""
echo -e "${BLUE}🔍 QUE BUSCAR:${NC}"
echo -e "${GREEN}   • Widget con 3 columnas: [Número] [🟢 Operativo] [Hora]${NC}"
echo -e "${GREEN}   • El dropdown debe mostrar \"🌐 Todas las APIs\" PRIMERO${NC}"
echo -e "${GREEN}   • Los grupos deben tener nombres mejorados${NC}"
echo -e "${GREEN}   • En Console: mensajes de carga del JavaScript${NC}"
echo ""
echo -e "${YELLOW}⚠️  SI NO VES EL WIDGET:${NC}"
echo -e "${RED}   1. Verifica que CSS/JS cargan (códigos 200 arriba)${NC}"
echo -e "${RED}   2. Revisa Console de navegador por errores${NC}"
echo -e "${RED}   3. Refresca la página (Ctrl+F5)${NC}"
echo -e "${RED}   4. Asegúrate de estar en \"🌐 Todas las APIs\"${NC}"
