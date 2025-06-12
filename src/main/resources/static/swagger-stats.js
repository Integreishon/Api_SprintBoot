/**
 * Widget de estadísticas dinámicas para Swagger UI
 * Muestra datos reales del sistema de forma minimalista
 */

console.log('🔧 Cargando widget de estadísticas...');

// Función para cargar estadísticas reales
async function loadApiStats() {
    try {
        console.log('📊 Intentando cargar estadísticas...');
        
        // Primero intentar el endpoint de stats
        const response = await fetch('/api/admin/api-status/stats');
        if (response.ok) {
            const data = await response.json();
            console.log('✅ Stats obtenidas del endpoint:', data);
            return {
                totalEndpoints: data.data.totalEndpoints,
                systemStatus: "🟢 Operativo",
                lastUpdated: new Date().toLocaleTimeString()
            };
        }
    } catch (error) {
        console.log('⚠️ Endpoint de stats no disponible, usando conteo manual');
    }
    
    // Fallback: contar desde documentación OpenAPI
    return await countFromOpenAPI();
}

// Función para contar endpoints desde OpenAPI
async function countFromOpenAPI() {
    try {
        console.log('📋 Contando endpoints desde OpenAPI...');
        const response = await fetch('/api/v3/api-docs');
        const openApiDoc = await response.json();
        
        const paths = openApiDoc.paths || {};
        const totalEndpoints = Object.keys(paths).reduce((total, path) => {
            const methods = Object.keys(paths[path]);
            return total + methods.length;
        }, 0);
        
        console.log(`✅ Contados ${totalEndpoints} endpoints`);
        
        return {
            totalEndpoints: totalEndpoints,
            systemStatus: "🟢 Operativo",
            lastUpdated: new Date().toLocaleTimeString()
        };
    } catch (error) {
        console.error('❌ Error contando endpoints:', error);
        return {
            totalEndpoints: 0,
            systemStatus: "⚠️ Error",
            lastUpdated: new Date().toLocaleTimeString()
        };
    }
}

// Crear widget HTML
function createStatsWidget(stats) {
    return `
        <div class="api-stats-widget" style="
            background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
            border-radius: 8px;
            padding: 1.25rem;
            margin: 1.5rem 0;
            border: 1px solid #cbd5e1;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        ">
            <div style="
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 1rem;
                flex-wrap: wrap;
            ">
                <div style="text-align: center; flex: 1; min-width: 100px;">
                    <div style="
                        font-size: 1.75rem;
                        font-weight: 700;
                        color: #2563eb;
                        line-height: 1;
                        margin-bottom: 0.25rem;
                    ">${stats.totalEndpoints}</div>
                    <div style="
                        font-size: 0.75rem;
                        color: #6b7280;
                        text-transform: uppercase;
                        letter-spacing: 0.05em;
                        font-weight: 500;
                    ">Endpoints</div>
                </div>
                
                <div style="text-align: center; flex: 1; min-width: 120px;">
                    <div style="
                        font-size: 1rem;
                        font-weight: 600;
                        color: #059669;
                        line-height: 1;
                        margin-bottom: 0.25rem;
                    ">${stats.systemStatus}</div>
                    <div style="
                        font-size: 0.75rem;
                        color: #6b7280;
                        text-transform: uppercase;
                        letter-spacing: 0.05em;
                        font-weight: 500;
                    ">Estado</div>
                </div>
                
                <div style="text-align: center; flex: 1; min-width: 100px;">
                    <div style="
                        font-size: 0.875rem;
                        font-weight: 500;
                        color: #374151;
                        line-height: 1;
                        margin-bottom: 0.25rem;
                    ">${stats.lastUpdated}</div>
                    <div style="
                        font-size: 0.75rem;
                        color: #6b7280;
                        text-transform: uppercase;
                        letter-spacing: 0.05em;
                        font-weight: 500;
                    ">Actualizado</div>
                </div>
            </div>
        </div>
    `;
}

// Función para insertar el widget
function insertStatsWidget() {
    console.log('🎯 Insertando widget de estadísticas...');
    
    // Buscar el contenedor de descripción
    const descriptionContainer = document.querySelector('.swagger-ui .info .description');
    
    if (descriptionContainer && !document.querySelector('.api-stats-widget')) {
        console.log('📍 Contenedor encontrado, cargando estadísticas...');
        
        loadApiStats().then(stats => {
            console.log('📊 Datos obtenidos:', stats);
            const widget = createStatsWidget(stats);
            descriptionContainer.insertAdjacentHTML('afterend', widget);
            console.log('✅ Widget insertado exitosamente');
        });
    } else if (document.querySelector('.api-stats-widget')) {
        console.log('ℹ️ Widget ya existe');
    } else {
        console.log('❌ No se encontró contenedor de descripción');
    }
}

// Función para verificar si estamos en "Todas las APIs"
function isAllAPIsGroup() {
    const url = window.location.href;
    // Verificar si estamos en el grupo "all" o en la página principal
    return url.includes('01-all') || (!url.includes('#') && !url.includes('02-') && !url.includes('03-'));
}

// Función principal
function enhanceSwaggerWithStats() {
    console.log('🚀 Iniciando mejoras de Swagger...');
    
    if (isAllAPIsGroup()) {
        console.log('✅ Estamos en "Todas las APIs", insertando widget...');
        setTimeout(insertStatsWidget, 1000); // Delay para asegurar que Swagger UI esté completamente cargado
    } else {
        console.log('ℹ️ No estamos en "Todas las APIs", saltando widget');
    }
}

// Ejecutar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', enhanceSwaggerWithStats);
} else {
    enhanceSwaggerWithStats();
}

// Observer para cambios de URL
let lastUrl = location.href;
const observer = new MutationObserver(() => {
    const url = location.href;
    if (url !== lastUrl) {
        lastUrl = url;
        console.log('🔄 URL cambió a:', url);
        setTimeout(enhanceSwaggerWithStats, 1500); // Más tiempo para que Swagger UI se actualice
    }
});

observer.observe(document, { 
    subtree: true, 
    childList: true 
});

// Actualizar estadísticas cada 30 segundos
setInterval(() => {
    const widget = document.querySelector('.api-stats-widget');
    if (widget && isAllAPIsGroup()) {
        console.log('🔄 Actualizando estadísticas...');
        loadApiStats().then(stats => {
            const newContent = createStatsWidget(stats);
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = newContent;
            widget.outerHTML = tempDiv.innerHTML;
            console.log('✅ Estadísticas actualizadas');
        });
    }
}, 30000);

console.log('✅ Script de estadísticas cargado completamente');
