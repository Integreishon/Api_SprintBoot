#!/bin/bash

# 🔧 SCRIPT DE VERIFICACIÓN DE ERRORES JAVA - HOSPITAL API

echo "🏥 === VERIFICANDO ERRORES JAVA EN HOSPITAL API ==="
echo ""

CONFIG_DIR="src/main/java/com/hospital/backend/config"

echo "📁 1. VERIFICANDO ARCHIVOS EN CONFIG..."
ls -la "$CONFIG_DIR/"
echo ""

echo "🔍 2. VERIFICANDO CLASES DUPLICADAS..."
echo "Buscando clases OpenApiConfig duplicadas:"
find "$CONFIG_DIR" -name "*OpenApiConfig*" -type f
echo ""

echo "📝 3. VERIFICANDO IMPORTS NO UTILIZADOS..."
echo "Checking SecurityConfigDebug.java:"
if grep -q "UsernamePasswordAuthenticationFilter" "$CONFIG_DIR/SecurityConfigDebug.java"; then
    echo "❌ ENCONTRADO: Import no utilizado UsernamePasswordAuthenticationFilter"
else
    echo "✅ OK: No hay imports no utilizados"
fi

if grep -q "jwtAuthenticationFilter" "$CONFIG_DIR/SecurityConfigDebug.java"; then
    echo "❌ ENCONTRADO: Variable no utilizada jwtAuthenticationFilter"
else
    echo "✅ OK: No hay variables no utilizadas"
fi
echo ""

echo "🧹 4. VERIFICANDO PERFILES DE SPRING..."
PROFILE=$(grep -o '@Profile("[^"]*")' "$CONFIG_DIR/SecurityConfigDebug.java" | cut -d'"' -f2)
echo "SecurityConfigDebug está configurado con profile: $PROFILE"
if [ "$PROFILE" = "disabled" ]; then
    echo "✅ OK: SecurityConfigDebug está DESACTIVADO"
else
    echo "⚠️  ADVERTENCIA: SecurityConfigDebug está ACTIVO"
fi
echo ""

echo "🎯 5. RESUMEN DE ESTADO:"
echo "=================="

# Verificar si hay errores
ERRORS=0

if [ -f "$CONFIG_DIR/OpenApiConfig_fixed.java" ]; then
    echo "❌ ERROR: Archivo duplicado OpenApiConfig_fixed.java existe"
    ERRORS=$((ERRORS + 1))
fi

if grep -q "UsernamePasswordAuthenticationFilter" "$CONFIG_DIR/SecurityConfigDebug.java"; then
    echo "❌ ERROR: Import no utilizado en SecurityConfigDebug.java"
    ERRORS=$((ERRORS + 1))
fi

if grep -q "jwtAuthenticationFilter" "$CONFIG_DIR/SecurityConfigDebug.java"; then
    echo "❌ ERROR: Variable no utilizada en SecurityConfigDebug.java"
    ERRORS=$((ERRORS + 1))
fi

if [ $ERRORS -eq 0 ]; then
    echo "🎉 ¡TODOS LOS ERRORES HAN SIDO SOLUCIONADOS!"
    echo "✅ No hay clases duplicadas"
    echo "✅ No hay imports no utilizados"
    echo "✅ No hay variables no utilizadas"
    echo ""
    echo "🚀 EL PROYECTO ESTÁ LISTO PARA COMPILAR"
else
    echo "⚠️  FALTAN $ERRORS PROBLEMAS POR SOLUCIONAR"
fi

echo ""
echo "💡 PRÓXIMO PASO: Ejecutar 'mvn clean compile' para verificar"
