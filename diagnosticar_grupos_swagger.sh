#!/bin/bash

echo "🔍 === DIAGNÓSTICO DE GRUPOS SWAGGER ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Iniciando diagnóstico de grupos Swagger...${NC}"
echo ""

# Compilar primero
echo -e "${YELLOW}1. Compilando proyecto...${NC}"
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló la compilación${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilación exitosa${NC}"
echo ""

echo -e "${YELLOW}2. Iniciando aplicación en segundo plano...${NC}"
mvn spring-boot:run > /dev/null 2>&1 &
APP_PID=$!

echo -e "${GREEN}✅ Aplicación iniciada (PID: $APP_PID)${NC}"
echo -e "${YELLOW}⏳ Esperando 30 segundos para que inicie completamente...${NC}"

# Esperar a que la aplicación inicie
sleep 30

echo ""
echo -e "${YELLOW}3. Probando endpoints de diagnóstico...${NC}"

# Probar endpoint principal de API docs
echo -e "${BLUE}📊 Probando /v3/api-docs...${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v3/api-docs)

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ /v3/api-docs responde correctamente (200)${NC}"
else
    echo -e "${RED}❌ /v3/api-docs falló (HTTP: $HTTP_CODE)${NC}"
fi

# Probar grupos individuales
GROUPS=("authentication" "patients" "appointments" "specialties" "all")

echo ""
echo -e "${BLUE}🔍 Probando grupos individuales...${NC}"

for group in "${GROUPS[@]}"; do
    echo -e "${YELLOW}Probando grupo: $group${NC}"
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/v3/api-docs/$group")
    
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}  ✅ Grupo '$group' funciona (HTTP: $HTTP_CODE)${NC}"
        
        # Contar endpoints en este grupo
        ENDPOINT_COUNT=$(curl -s "http://localhost:8080/api/v3/api-docs/$group" | grep -o '"\/[^"]*"' | wc -l)
        echo -e "${BLUE}     📈 Endpoints encontrados: $ENDPOINT_COUNT${NC}"
    else
        echo -e "${RED}  ❌ Grupo '$group' falló (HTTP: $HTTP_CODE)${NC}"
    fi
done

echo ""
echo -e "${YELLOW}4. Verificando swagger-config...${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v3/api-docs/swagger-config)

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ swagger-config responde correctamente${NC}"
    
    echo -e "${BLUE}📋 Contenido de swagger-config:${NC}"
    curl -s http://localhost:8080/api/v3/api-docs/swagger-config | python3 -m json.tool 2>/dev/null || echo "No se pudo formatear JSON"
else
    echo -e "${RED}❌ swagger-config falló (HTTP: $HTTP_CODE)${NC}"
fi

echo ""
echo -e "${YELLOW}5. Probando Swagger UI...${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/swagger-ui/index.html)

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ Swagger UI carga correctamente${NC}"
else
    echo -e "${RED}❌ Swagger UI falló (HTTP: $HTTP_CODE)${NC}"
fi

echo ""
echo -e "${YELLOW}6. Deteniendo aplicación...${NC}"
kill $APP_PID
wait $APP_PID 2>/dev/null

echo -e "${GREEN}✅ Aplicación detenida${NC}"
echo ""

echo -e "${BLUE}📋 RESUMEN DEL DIAGNÓSTICO:${NC}"
echo ""
echo -e "${GREEN}✅ URLs para probar manualmente:${NC}"
echo -e "${YELLOW}   🌐 Swagger UI: http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "${YELLOW}   📊 API Docs general: http://localhost:8080/api/v3/api-docs${NC}"
echo -e "${YELLOW}   🔐 Grupo Autenticación: http://localhost:8080/api/v3/api-docs/authentication${NC}"
echo -e "${YELLOW}   👥 Grupo Pacientes: http://localhost:8080/api/v3/api-docs/patients${NC}"
echo -e "${YELLOW}   📅 Grupo Citas: http://localhost:8080/api/v3/api-docs/appointments${NC}"
echo ""
echo -e "${BLUE}🔍 PARA DEBUGGING MANUAL:${NC}"
echo -e "${YELLOW}1. Inicia la aplicación: mvn spring-boot:run${NC}"
echo -e "${YELLOW}2. Ve a Swagger UI y abre las Dev Tools del navegador${NC}"
echo -e "${YELLOW}3. Ve a la pestaña Network${NC}"
echo -e "${YELLOW}4. Cambia entre grupos en 'Select a definition'${NC}"
echo -e "${YELLOW}5. Observa qué requests se hacen y si fallan${NC}"
echo ""
echo -e "${GREEN}🎯 Si los grupos aparecen en el selector pero no filtran:${NC}"
echo -e "${YELLOW}   - Problema probable en la configuración de springdoc${NC}"
echo -e "${YELLOW}   - Verifica que los paths coincidan exactamente${NC}"
echo -e "${YELLOW}   - Revisa que no haya conflictos de configuración${NC}"
