#!/bin/bash

echo "🔥 === SOLUCIÓN: ERROR DE COMPILACIÓN JAVA ==="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${RED}ERROR DETECTADO:${NC}"
echo -e "${RED}java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN${NC}"
echo ""
echo -e "${YELLOW}CAUSA: Incompatibilidad entre versión de Java y Maven${NC}"
echo ""

echo -e "${YELLOW}Paso 1: Verificando versiones de Java y Maven${NC}"
echo "-------------------------------------------"

echo -e "${BLUE}Java version:${NC}"
java -version

echo ""
echo -e "${BLUE}Javac version:${NC}"
javac -version

echo ""
echo -e "${BLUE}Maven version:${NC}"
mvn -version

echo ""
echo -e "${YELLOW}Paso 2: Solucionando el problema de compilación${NC}"
echo "--------------------------------------------"

# Opción 1: Usar Maven Wrapper en lugar de Maven global
if [ -f "mvnw" ]; then
    echo -e "${GREEN}✅ Maven Wrapper encontrado, usándolo...${NC}"
    
    echo -e "${YELLOW}Limpiando con Maven Wrapper...${NC}"
    ./mvnw clean
    
    echo -e "${YELLOW}Compilando con Maven Wrapper...${NC}"
    ./mvnw compile
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ ¡Compilación exitosa con Maven Wrapper!${NC}"
        echo ""
        echo -e "${YELLOW}Iniciando aplicación...${NC}"
        ./mvnw spring-boot:run
    else
        echo -e "${RED}❌ Maven Wrapper también falló${NC}"
        echo ""
        echo -e "${YELLOW}Probando solución alternativa...${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Maven Wrapper no encontrado${NC}"
fi

echo ""
echo -e "${YELLOW}Paso 3: Solución alternativa - Forzar versión de Java${NC}"
echo "---------------------------------------------------"

# Crear un script temporal que fuerce la versión de Java
echo -e "${YELLOW}Ejecutando con configuración específica de Java...${NC}"

# Configurar variables de entorno para Java
export JAVA_HOME="C:/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

echo -e "${YELLOW}Intentando compilación con Java específico...${NC}"
mvn clean compile -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 -Dmaven.compiler.release=17

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ ¡Compilación exitosa!${NC}"
    echo ""
    echo -e "${YELLOW}Iniciando aplicación...${NC}"
    mvn spring-boot:run
else
    echo -e "${RED}❌ Aún hay problemas de compilación${NC}"
    echo ""
    echo -e "${YELLOW}Mostrando detalles del error:${NC}"
    mvn compile -e -X | grep -A 10 -B 10 "ERROR"
fi
