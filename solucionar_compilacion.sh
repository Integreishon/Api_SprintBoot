#!/bin/bash

echo "🔧 === SOLUCIONANDO PROBLEMAS DE COMPILACIÓN ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}1. Verificando versión de Java...${NC}"
java -version

echo ""
echo -e "${YELLOW}2. Limpiando caché de Maven...${NC}"

# Limpiar el repositorio local de Maven (solo las dependencias problemáticas)
echo -e "${BLUE}Limpiando caché de Maven local...${NC}"
rm -rf ~/.m2/repository/org/apache/maven/plugins/maven-compiler-plugin/3.8.1/
rm -rf ~/.m2/repository/com/google/guava/

echo ""
echo -e "${YELLOW}3. Regenerando Maven Wrapper...${NC}"

# Eliminar wrapper existente
rm -f mvnw mvnw.cmd
rm -rf .mvn/

# Regenerar wrapper con versión más reciente
mvn wrapper:wrapper -Dmaven=3.9.9

echo ""
echo -e "${YELLOW}4. Limpiando proyecto completamente...${NC}"

# Limpiar proyecto
./mvnw clean -X

echo ""
echo -e "${YELLOW}5. Forzando descarga de dependencias...${NC}"

# Forzar descarga de dependencias con versiones actualizadas
./mvnw dependency:purge-local-repository -DreResolve=true

echo ""
echo -e "${YELLOW}6. Compilando con configuración actualizada...${NC}"

# Compilar con configuración específica para Java 17
./mvnw compile -DskipTests -X \
  -Dmaven.compiler.source=17 \
  -Dmaven.compiler.target=17 \
  -Dmaven.compiler.release=17

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ ¡COMPILACIÓN EXITOSA!${NC}"
    echo ""
    echo -e "${YELLOW}Ahora puedes iniciar la aplicación:${NC}"
    echo -e "${GREEN}./mvnw spring-boot:run${NC}"
else
    echo ""
    echo -e "${RED}❌ ERROR EN COMPILACIÓN${NC}"
    echo ""
    echo -e "${YELLOW}Intentos alternativos:${NC}"
    echo ""
    echo -e "${BLUE}1. Verificar versión de Java:${NC}"
    echo -e "   java -version"
    echo -e "   (Debe ser Java 17)"
    echo ""
    echo -e "${BLUE}2. Usar Maven del sistema:${NC}"
    echo -e "   mvn clean compile -DskipTests"
    echo ""
    echo -e "${BLUE}3. Verificar JAVA_HOME:${NC}"
    echo -e "   echo \$JAVA_HOME"
    echo -e "   (Debe apuntar a Java 17)"
fi
