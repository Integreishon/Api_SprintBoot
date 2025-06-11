#!/bin/bash

echo "================================================"
echo "🔧 SOLUCIONANDO WARNINGS DE CONFIGURACIÓN JWT"
echo "================================================"

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo ""
echo -e "${BLUE}1. Limpiando proyecto...${NC}"
mvn clean

echo ""
echo -e "${BLUE}2. Compilando proyecto para generar metadatos...${NC}"
mvn compile -q

echo ""
echo -e "${BLUE}3. Verificando que no hay warnings de propiedades...${NC}"
WARNINGS=$(mvn compile 2>&1 | grep "unknown property" | wc -l)

if [ "$WARNINGS" -eq 0 ]; then
    echo -e "${GREEN}✅ ¡ÉXITO! No se encontraron warnings de propiedades desconocidas${NC}"
else
    echo -e "${RED}❌ Aún hay $WARNINGS warnings de propiedades:${NC}"
    mvn compile 2>&1 | grep "unknown property"
fi

echo ""
echo -e "${BLUE}4. Verificando metadatos generados...${NC}"
if [ -f "target/classes/META-INF/spring-configuration-metadata.json" ]; then
    echo -e "${GREEN}✅ Metadatos de configuración generados automáticamente${NC}"
    echo "Contenido de metadatos:"
    cat target/classes/META-INF/spring-configuration-metadata.json | head -20
else
    echo -e "${YELLOW}⚠️ Metadatos no generados automáticamente, usando archivo manual${NC}"
fi

echo ""
echo -e "${BLUE}5. Verificando propiedades en application.properties...${NC}"
echo "Propiedades JWT configuradas:"
grep "^app\.jwt\." src/main/resources/application.properties | sed 's/^/  /'

echo ""
echo -e "${BLUE}6. Intentando iniciar aplicación para verificar configuración...${NC}"
timeout 10s mvn spring-boot:run > /dev/null 2>&1 &
SPRING_PID=$!
sleep 8

if ps -p $SPRING_PID > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Aplicación inicia correctamente${NC}"
    kill $SPRING_PID > /dev/null 2>&1
else
    echo -e "${RED}❌ Error al iniciar aplicación${NC}"
fi

echo ""
echo "================================================"
echo "Verificación completada"
echo "================================================"
