#!/bin/bash

echo "🔍 === VERIFICACIÓN FINAL DE SWAGGER LIMPIO ==="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}1. Verificando archivos de configuración...${NC}"

# Verificar que los archivos problemáticos han sido removidos
PROBLEMATIC_FILES=(
    "src/main/java/com/hospital/backend/config/SwaggerConfig.java"
    "src/main/java/com/hospital/backend/config/SwaggerController.java"
    "src/main/java/com/hospital/backend/config/SwaggerIndexController.java" 
    "src/main/java/com/hospital/backend/config/SwaggerRedirectConfig.java"
    "src/main/java/com/hospital/backend/config/SecurityConfigDebug.java"
    "src/main/resources/static/swagger-ui-custom.css"
    "src/main/resources/static/swagger-ui-custom.js"
    "src/main/resources/static/swagger-fix.json"
)

for file in "${PROBLEMATIC_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${RED}❌ ERROR: Archivo problemático aún existe: $file${NC}"
        exit 1
    else
        echo -e "${GREEN}✅ Archivo problemático removido: $file${NC}"
    fi
done

echo ""
echo -e "${YELLOW}2. Verificando archivos de backup...${NC}"

# Verificar que los archivos están en backup
if [ -d "backup_debug" ]; then
    echo -e "${GREEN}✅ Directorio backup_debug existe${NC}"
    BACKUP_COUNT=$(find backup_debug -name "*.backup" -o -name "*.java" -o -name "*.css" -o -name "*.js" -o -name "*.json" -o -name "*.sh" -o -name "*.md" -o -name "*.sql" -o -name "*.xml" | wc -l)
    echo -e "${GREEN}✅ Archivos en backup: $BACKUP_COUNT${NC}"
else
    echo -e "${RED}❌ ERROR: Directorio backup_debug no existe${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}3. Verificando configuración final...${NC}"

# Verificar OpenApiConfig.java
if [ -f "src/main/java/com/hospital/backend/config/OpenApiConfig.java" ]; then
    echo -e "${GREEN}✅ OpenApiConfig.java existe${NC}"
    
    # Verificar que no contiene configuraciones problemáticas
    if grep -q "SwaggerController\|SwaggerRedirectConfig\|swagger-ui-custom" "src/main/java/com/hospital/backend/config/OpenApiConfig.java"; then
        echo -e "${RED}❌ ERROR: OpenApiConfig.java contiene referencias problemáticas${NC}"
        exit 1
    else
        echo -e "${GREEN}✅ OpenApiConfig.java está limpio${NC}"
    fi
else
    echo -e "${RED}❌ ERROR: OpenApiConfig.java no existe${NC}"
    exit 1
fi

# Verificar WebConfig.java
if [ -f "src/main/java/com/hospital/backend/config/WebConfig.java" ]; then
    echo -e "${GREEN}✅ WebConfig.java existe${NC}"
    
    # Verificar que está simplificado
    if grep -q "addViewControllers\|addResourceHandlers.*swagger" "src/main/java/com/hospital/backend/config/WebConfig.java"; then
        echo -e "${RED}❌ ERROR: WebConfig.java contiene configuraciones de Swagger${NC}"
        exit 1
    else
        echo -e "${GREEN}✅ WebConfig.java está simplificado${NC}"
    fi
else
    echo -e "${RED}❌ ERROR: WebConfig.java no existe${NC}"
    exit 1
fi

# Verificar application.properties
if [ -f "src/main/resources/application.properties" ]; then
    echo -e "${GREEN}✅ application.properties existe${NC}"
    
    # Verificar configuraciones básicas de Swagger
    if grep -q "springdoc.api-docs.path=/v3/api-docs" "src/main/resources/application.properties" && \
       grep -q "springdoc.swagger-ui.path=/swagger-ui/index.html" "src/main/resources/application.properties"; then
        echo -e "${GREEN}✅ application.properties tiene configuración básica de Swagger${NC}"
    else
        echo -e "${RED}❌ ERROR: application.properties no tiene configuración correcta de Swagger${NC}"
        exit 1
    fi
    
    # Verificar que no hay configuraciones duplicadas problemáticas
    if grep -q "configUrl\|swagger-config" "src/main/resources/application.properties"; then
        echo -e "${RED}❌ ERROR: application.properties contiene configuraciones problemáticas${NC}"
        exit 1
    else
        echo -e "${GREEN}✅ application.properties no contiene configuraciones problemáticas${NC}"
    fi
else
    echo -e "${RED}❌ ERROR: application.properties no existe${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}4. Verificando directorio target...${NC}"

if [ -d "target" ]; then
    echo -e "${YELLOW}⚠️  ADVERTENCIA: Directorio target existe (debería ser regenerado)${NC}"
    echo -e "${YELLOW}   Recomendación: Ejecutar 'mvn clean' antes de compilar${NC}"
else
    echo -e "${GREEN}✅ Directorio target ha sido limpiado${NC}"
fi

echo ""
echo -e "${YELLOW}5. Verificando estructura final del proyecto...${NC}"

REQUIRED_FILES=(
    "pom.xml"
    "src/main/java/com/hospital/backend/HospitalApiApplication.java"
    "src/main/java/com/hospital/backend/config/OpenApiConfig.java"
    "src/main/java/com/hospital/backend/config/WebConfig.java"
    "src/main/java/com/hospital/backend/config/SecurityConfig.java"
    "src/main/resources/application.properties"
)

for file in "${REQUIRED_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✅ $file${NC}"
    else
        echo -e "${RED}❌ ERROR: Archivo requerido faltante: $file${NC}"
        exit 1
    fi
done

echo ""
echo -e "${GREEN}🎉 ¡VERIFICACIÓN COMPLETA EXITOSA!${NC}"
echo ""
echo -e "${YELLOW}📋 INSTRUCCIONES PARA CONTINUAR:${NC}"
echo ""
echo -e "${YELLOW}1. Compilar proyecto limpio:${NC}"
echo -e "   ${GREEN}mvn clean compile${NC}"
echo ""
echo -e "${YELLOW}2. Iniciar aplicación:${NC}"
echo -e "   ${GREEN}mvn spring-boot:run${NC}"
echo ""
echo -e "${YELLOW}3. Acceder a Swagger UI:${NC}"
echo -e "   ${GREEN}http://localhost:8080/api/swagger-ui/index.html${NC}"
echo ""
echo -e "${YELLOW}4. URLs que DEBEN funcionar:${NC}"
echo -e "   ${GREEN}✅ http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "   ${GREEN}✅ http://localhost:8080/api/v3/api-docs${NC}"
echo ""
echo -e "${YELLOW}5. URLs que NO deben existir (error esperado):${NC}"
echo -e "   ${RED}❌ http://localhost:8080/api/swagger-ui/swagger-ui/index.html${NC}"
echo -e "   ${RED}❌ http://localhost:8080/v3/api-docs/swagger-config${NC}"
echo ""
echo -e "${GREEN}🚀 ¡Todo listo para probar Swagger limpio!${NC}"
