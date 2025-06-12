/**
 * Widget dinámico de estadísticas para Swagger UI
 * Obtiene datos reales del sistema y los muestra de forma minimalista
 */

// Función para cargar estadísticas reales
async function loadApiStats() {
    try {
        const response = await fetch('/api/admin/api-status/stats');
        const data = await response.json();
        
        if (data.success && data.data) {
            return data.data;
        }
    } catch (error) {
        console.log('Stats endpoint no disponible aún');
    }
    
    // Fallback: contar manualmente desde la documentación actual
    return await countCurrentEndpoints();
}

// Función para contar endpoints reales desde la documentación de Swagger
async function countCurrentEndpoints() {
    try {
        const response = await fetch('/api/v3/api-docs');
        const openApiDoc = await response.json();
        
        const paths = openApiDoc.paths || {};
        const totalEndpoints = Object.keys(paths).reduce((total, path) => {
            const methods = Object.keys(paths[path]);
            return total + methods.length;
        }, 0);
        
        return {
            totalEndpoints: totalEndpoints,
            systemStatus: "🟢 Operativo",
            lastUpdated: new Date().toLocaleString()
        };
    } catch (error) {
        return {
            totalEndpoints: 0,
            systemStatus: "⚠️ Cargando...",
            lastUpdated: new Date().toLocaleString()
        };
    }
}

// Función para crear el widget de estadísticas
function createStatsWidget(stats) {
    return `
        <div class="api-stats-widget">
            <div class="stats-row">
                <div class="stat-item">
                    <span class="stat-number">${stats.totalEndpoints}</span>
                    <span class="stat-label">Endpoints</span>
                </div>
                <div class="stat-item">
                    <span class="stat-status">${stats.systemStatus}</span>
                    <span class="stat-label">Estado</span>
                </div>
                <div class="stat-item">
                    <span class="stat-time">${stats.lastUpdated}</span>
                    <span class="stat-label">Actualizado</span>
                </div>
            </div>
        </div>
    `;
}

// CSS para el widget
const widgetStyles = `
<style>
.api-stats-widget {
    background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
    border-radius: 8px;
    padding: 1rem;
    margin: 1rem 0;
    border: 1px solid #cbd5e1;
}

.stats-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
}

.stat-item {
    text-align: center;
    flex: 1;
}

.stat-number {
    display: block;
    font-size: 1.5rem;
    font-weight: 700;
    color: #2563eb;
}

.stat-status {
    display: block;
    font-size: 1rem;
    font-weight: 600;
    color: #059669;
}

.stat-time {
    display: block;
    font-size: 0.875rem;
    font-weight: 500;
    color: #6b7280;
}

.stat-label {
    display: block;
    font-size: 0.75rem;
    color: #9ca3af;
    margin-top: 0.25rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

@media (max-width: 768px) {
    .stats-row {
        flex-direction: column;
        gap: 0.75rem;
    }
    
    .stat-item {
        padding: 0.5rem 0;
        border-bottom: 1px solid #e2e8f0;
    }
    
    .stat-item:last-child {
        border-bottom: none;
    }
}
</style>
`;

// Función principal que se ejecuta cuando Swagger UI está listo
function enhanceSwaggerWithStats() {
    // Solo actuar en el grupo "all" (Todas las APIs)
    const currentUrl = window.location.href;
    const isAllGroup = currentUrl.includes('/all') || (!currentUrl.includes('#') && !currentUrl.includes('/authentication') && !currentUrl.includes('/users') && !currentUrl.includes('/appointments'));
    
    if (!isAllGroup) return;
    
    // Buscar el contenedor de descripción
    const descriptionContainer = document.querySelector('.swagger-ui .info .description');
    
    if (descriptionContainer && !document.querySelector('.api-stats-widget')) {
        // Agregar estilos
        if (!document.querySelector('#stats-widget-styles')) {
            const styleElement = document.createElement('div');
            styleElement.id = 'stats-widget-styles';
            styleElement.innerHTML = widgetStyles;
            document.head.appendChild(styleElement);
        }
        
        // Cargar y mostrar estadísticas
        loadApiStats().then(stats => {
            const widget = createStatsWidget(stats);
            descriptionContainer.insertAdjacentHTML('afterend', widget);
        });
    }
}

// Ejecutar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', enhanceSwaggerWithStats);
} else {
    enhanceSwaggerWithStats();
}

// También ejecutar cuando cambie la URL (navegación entre grupos)
let lastUrl = location.href;
new MutationObserver(() => {
    const url = location.href;
    if (url !== lastUrl) {
        lastUrl = url;
        setTimeout(enhanceSwaggerWithStats, 500); // Pequeño delay para que Swagger UI termine de cargar
    }
}).observe(document, { subtree: true, childList: true });

// Actualizar estadísticas cada 30 segundos
setInterval(() => {
    const widget = document.querySelector('.api-stats-widget');
    if (widget) {
        loadApiStats().then(stats => {
            widget.innerHTML = createStatsWidget(stats).replace(/<div class="api-stats-widget">|<\/div>$/g, '');
        });
    }
}, 30000);
