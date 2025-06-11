#!/bin/bash

echo "==================================="
echo "Verificación de Fixes de Warnings"
echo "==================================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para verificar si un archivo contiene una línea específica
check_file_not_contains() {
    local file=$1
    local pattern=$2
    local description=$3
    
    if grep -q "$pattern" "$file" 2>/dev/null; then
        echo -e "${RED}❌ FALLA:${NC} $description"
        echo "   Archivo: $file"
        echo "   Aún contiene: $pattern"
        return 1
    else
        echo -e "${GREEN}✅ OK:${NC} $description"
        return 0
    fi
}

# Función para verificar si un archivo contiene una línea específica
check_file_contains() {
    local file=$1
    local pattern=$2
    local description=$3
    
    if grep -q "$pattern" "$file" 2>/dev/null; then
        echo -e "${GREEN}✅ OK:${NC} $description"
        return 0
    else
        echo -e "${RED}❌ FALLA:${NC} $description"
        echo "   Archivo: $file"
        echo "   No contiene: $pattern"
        return 1
    fi
}

echo ""
echo "1. Verificando configuración de propiedades JWT..."
check_file_contains "src/main/resources/application.properties" "app.jwt.secret" "Propiedades JWT actualizadas"
check_file_not_contains "src/main/resources/application.properties" "^jwt\.secret" "Propiedades JWT antiguas removidas"

echo ""
echo "2. Verificando JwtProperties.java..."
check_file_contains "src/main/java/com/hospital/backend/config/JwtProperties.java" "@Component" "Anotación @Component agregada"
check_file_contains "src/main/java/com/hospital/backend/config/JwtProperties.java" "app.jwt" "Prefix actualizado a app.jwt"

echo ""
echo "3. Verificando OpenApiConfig.java..."
check_file_contains "src/main/java/com/hospital/backend/config/OpenApiConfig.java" "@Configuration" "OpenApiConfig creado"

echo ""
echo "4. Verificando imports removidos..."
check_file_not_contains "src/main/java/com/hospital/backend/chatbot/service/ChatbotService.java" "import com.hospital.backend.chatbot.entity.ChatbotKnowledgeBase;" "ChatbotKnowledgeBase import removido"
check_file_not_contains "src/main/java/com/hospital/backend/payment/dto/PaymentSummaryResponse.java" "import com.hospital.backend.enums.PaymentMethodType;" "PaymentMethodType import removido"
check_file_not_contains "src/main/java/com/hospital/backend/user/controller/PatientController.java" "import io.swagger.v3.oas.annotations.Parameter;" "Parameter import removido"
check_file_not_contains "src/main/java/com/hospital/backend/catalog/dto/request/DocumentTypeRequest.java" "import jakarta.validation.constraints.Pattern;" "Pattern import removido"
check_file_not_contains "src/main/java/com/hospital/backend/appointment/service/AppointmentService.java" "import java.math.BigDecimal;" "BigDecimal import removido"
check_file_not_contains "src/main/java/com/hospital/backend/user/repository/DoctorAvailabilityRepository.java" "import java.time.LocalDate;" "LocalDate import removido"
check_file_not_contains "src/main/java/com/hospital/backend/analytics/entity/AuditLog.java" "import java.time.LocalDateTime;" "LocalDateTime import removido"
check_file_not_contains "src/main/java/com/hospital/backend/notification/service/NotificationService.java" "import org.springframework.data.domain.PageImpl;" "PageImpl import removido"
check_file_not_contains "src/main/java/com/hospital/backend/config/WebConfig.java" "import org.springframework.web.servlet.config.annotation.CorsRegistry;" "CorsRegistry import removido"

echo ""
echo "5. Verificando uso de variables..."
check_file_contains "src/main/java/com/hospital/backend/chatbot/service/ChatbotService.java" "@SuppressWarnings" "Type safety fix aplicado"
check_file_contains "src/main/java/com/hospital/backend/appointment/service/AppointmentService.java" "log.debug.*paciente.*encontrado" "Variable patient usada"
check_file_contains "src/main/java/com/hospital/backend/appointment/service/AvailabilityService.java" "log.debug.*doctor.*encontrado" "Variable doctor usada"
check_file_contains "src/main/java/com/hospital/backend/medical/service/PrescriptionService.java" "log.debug.*prescription.*ID" "Variable prescription usada"

echo ""
echo "==================================="
echo "Resumen de Verificación"
echo "==================================="

# Compilar para verificar que no hay errores
echo ""
echo "6. Compilando proyecto para verificar que no hay errores..."
if mvn clean compile -q; then
    echo -e "${GREEN}✅ COMPILACIÓN EXITOSA${NC}"
else
    echo -e "${RED}❌ ERRORES DE COMPILACIÓN${NC}"
fi

echo ""
echo "Verificación completada."
echo "Si todos los checks muestran ✅, los warnings han sido resueltos exitosamente."
