#!/bin/bash

echo "🔧 === SOLUCIONANDO ERROR DE MAIN CLASS ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${RED}ERROR DETECTADO:${NC} Could not find or load main class com.hospital.backend.HospitalApiApplication"
echo -e "${YELLOW}CAUSA:${NC} Directorio target corrupto o clases no compiladas"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ ERROR: No se encuentra pom.xml. Ejecuta este script desde la raíz del proyecto.${NC}"
    exit 1
fi

echo -e "${YELLOW}1. Verificando archivo principal de la aplicación...${NC}"

if [ -f "src/main/java/com/hospital/backend/HospitalApiApplication.java" ]; then
    echo -e "${GREEN}✅ HospitalApiApplication.java encontrado${NC}"
else
    echo -e "${RED}❌ ERROR: HospitalApiApplication.java no encontrado${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}2. Limpiando completamente el proyecto...${NC}"

# Eliminar target si existe
if [ -d "target" ]; then
    echo -e "${YELLOW}Removiendo directorio target corrupto...${NC}"
    rm -rf target
fi

# Limpiar con Maven
echo -e "${YELLOW}Ejecutando mvn clean...${NC}"
mvn clean -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló mvn clean${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Proyecto limpiado${NC}"
echo ""

echo -e "${YELLOW}3. Compilando proyecto desde cero...${NC}"

mvn compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERROR: Falló la compilación${NC}"
    echo ""
    echo -e "${YELLOW}Intentando compilación con más detalles...${NC}"
    mvn compile
    exit 1
fi

echo -e "${GREEN}✅ Proyecto compilado exitosamente${NC}"
echo ""

echo -e "${YELLOW}4. Verificando que la clase principal esté compilada...${NC}"

if [ -f "target/classes/com/hospital/backend/HospitalApiApplication.class" ]; then
    echo -e "${GREEN}✅ HospitalApiApplication.class encontrado en target/classes${NC}"
else
    echo -e "${RED}❌ ERROR: HospitalApiApplication.class no se compiló correctamente${NC}"
    echo -e "${YELLOW}Mostrando contenido de target/classes/com/hospital/backend/:${NC}"
    if [ -d "target/classes/com/hospital/backend" ]; then
        ls -la target/classes/com/hospital/backend/
    else
        echo -e "${RED}El directorio com/hospital/backend no existe en target/classes${NC}"
    fi
    exit 1
fi

echo ""
echo -e "${YELLOW}5. Verificando otras clases importantes...${NC}"

# Verificar algunas clases de configuración
CONFIG_CLASSES=(
    "target/classes/com/hospital/backend/config/OpenApiConfig.class"
    "target/classes/com/hospital/backend/config/SecurityConfig.class"
    "target/classes/com/hospital/backend/config/WebConfig.class"
)

for class_file in "${CONFIG_CLASSES[@]}"; do
    if [ -f "$class_file" ]; then
        echo -e "${GREEN}✅ $(basename $class_file)${NC}"
    else
        echo -e "${YELLOW}⚠️  $(basename $class_file) no encontrado${NC}"
    fi
done

echo ""
echo -e "${YELLOW}6. Ejecutando tests de compilación...${NC}"

mvn test-compile -q

if [ $? -ne 0 ]; then
    echo -e "${YELLOW}⚠️  WARNING: Test compilation failed, but main classes should work${NC}"
else
    echo -e "${GREEN}✅ Tests compilados correctamente${NC}"
fi

echo ""
echo -e "${GREEN}🎉 ¡PROBLEMA SOLUCIONADO!${NC}"
echo ""
echo -e "${BLUE}📋 Ahora puedes iniciar la aplicación con uno de estos comandos:${NC}"
echo ""
echo -e "${GREEN}Opción 1 (Recomendada):${NC}"
echo -e "   mvn spring-boot:run"
echo ""
echo -e "${GREEN}Opción 2 (Alternativa):${NC}"
echo -e "   java -jar target/*.jar"
echo ""
echo -e "${GREEN}Opción 3 (Con perfil específico):${NC}"
echo -e "   mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo ""
echo -e "${YELLOW}🌐 URLs que funcionarán una vez iniciado:${NC}"
echo -e "${GREEN}   Swagger UI: http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "${GREEN}   API Docs:   http://localhost:8080/api/v3/api-docs${NC}"
echo -e "${GREEN}   Health:     http://localhost:8080/api/actuator/health${NC}"
echo ""
echo -e "${BLUE}🚀 ¿Quieres iniciar la aplicación ahora? (y/n)${NC}"
read -r response

if [[ "$response" =~ ^[Yy]$ ]]; then
    echo ""
    echo -e "${GREEN}Iniciando aplicación...${NC}"
    echo ""
    mvn spring-boot:run
else
    echo ""
    echo -e "${YELLOW}Aplicación lista para iniciar cuando quieras.${NC}"
    echo -e "${GREEN}Ejecuta: mvn spring-boot:run${NC}"
fi
