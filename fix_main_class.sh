#!/bin/bash

echo "🔥 === SOLUCIÓN DIRECTA: MAIN CLASS ERROR ==="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${RED}ERROR: Could not find or load main class com.hospital.backend.HospitalApiApplication${NC}"
echo ""
echo -e "${YELLOW}SOLUCIONANDO AHORA...${NC}"
echo ""

# Paso 1: Eliminar todo lo compilado
echo -e "${YELLOW}1. Eliminando target corrupto...${NC}"
if [ -d "target" ]; then
    rm -rf target
    echo -e "${GREEN}   ✅ Target eliminado${NC}"
else
    echo -e "${GREEN}   ✅ Target no existía${NC}"
fi

# Paso 2: Limpiar con Maven
echo -e "${YELLOW}2. Limpiando con Maven...${NC}"
mvn clean > /dev/null 2>&1
echo -e "${GREEN}   ✅ Maven clean ejecutado${NC}"

# Paso 3: Compilar SOLO las clases principales
echo -e "${YELLOW}3. Compilando clases principales...${NC}"
mvn compile

if [ $? -eq 0 ]; then
    echo -e "${GREEN}   ✅ Compilación exitosa${NC}"
else
    echo -e "${RED}   ❌ ERROR en compilación${NC}"
    exit 1
fi

# Paso 4: Verificar que la clase principal existe
echo -e "${YELLOW}4. Verificando clase principal...${NC}"
if [ -f "target/classes/com/hospital/backend/HospitalApiApplication.class" ]; then
    echo -e "${GREEN}   ✅ HospitalApiApplication.class encontrado${NC}"
else
    echo -e "${RED}   ❌ ERROR: Clase principal no compilada${NC}"
    echo ""
    echo -e "${YELLOW}Mostrando contenido compilado:${NC}"
    find target/classes -name "*.class" | head -10
    exit 1
fi

# Paso 5: Intentar ejecutar directamente
echo -e "${YELLOW}5. Iniciando aplicación...${NC}"
echo ""
echo -e "${GREEN}🚀 EJECUTANDO: mvn spring-boot:run${NC}"
echo ""

mvn spring-boot:run
