/**
 * Mejoras avanzadas para Swagger UI
 * Este script añade funcionalidades profesionales a la interfaz
 */

// Espera a que el DOM esté listo
window.onload = function() {
  // Espera a que Swagger UI termine de cargarse
  const checkSwaggerUILoaded = setInterval(() => {
    if (document.querySelector('.swagger-ui')) {
      clearInterval(checkSwaggerUILoaded);
      enhanceSwaggerUI();
    }
  }, 100);
};

/**
 * Mejora la interfaz de Swagger UI con funcionalidades adicionales
 */
function enhanceSwaggerUI() {
  console.log('Enhancing Swagger UI with custom features...');
  
  // Añadir clase al body para estilos personalizados
  document.body.classList.add('hospital-api-docs');
  
  // Mejorar selector de definiciones
  enhanceDefinitionSelector();
  
  // Mejorar visualización de endpoints
  enhanceEndpoints();
  
  // Añadir barra de navegación rápida
  addQuickNavigation();
  
  // Añadir búsqueda avanzada
  enhanceSearch();
  
  // Añadir indicadores de estado
  addStatusIndicators();
  
  // Añadir contador de endpoints
  countEndpoints();
}

/**
 * Mejora el selector de definiciones
 */
function enhanceDefinitionSelector() {
  const selector = document.querySelector('.download-url-input');
  if (!selector) return;
  
  // Estilizar el selector
  selector.style.minWidth = '300px';
  
  // Añadir etiqueta visible
  const wrapper = selector.closest('.download-url-wrapper');
  if (wrapper) {
    const label = wrapper.querySelector('.select-label span');
    if (label) {
      label.textContent = 'Seleccionar Módulo:';
      label.style.fontWeight = 'bold';
      label.style.marginRight = '10px';
    }
  }
}

/**
 * Mejora la visualización de endpoints
 */
function enhanceEndpoints() {
  // Obtener todos los bloques de operación
  const opBlocks = document.querySelectorAll('.opblock');
  
  opBlocks.forEach(block => {
    // Añadir efecto hover
    block.addEventListener('mouseenter', function() {
      this.style.transform = 'translateY(-2px)';
      this.style.boxShadow = '0 4px 8px rgba(0,0,0,0.1)';
      this.style.transition = 'all 0.2s ease';
    });
    
    block.addEventListener('mouseleave', function() {
      this.style.transform = 'translateY(0)';
      this.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)';
    });
    
    // Añadir botón de copiar URL
    const summary = block.querySelector('.opblock-summary');
    if (summary) {
      const pathElement = summary.querySelector('.opblock-summary-path');
      if (pathElement) {
        const path = pathElement.dataset.path || pathElement.textContent;
        const method = block.className.match(/opblock-(\w+)/)[1].toUpperCase();
        
        const copyButton = document.createElement('button');
        copyButton.className = 'copy-path-button';
        copyButton.textContent = 'Copiar ruta';
        copyButton.style.fontSize = '11px';
        copyButton.style.padding = '2px 5px';
        copyButton.style.marginLeft = '10px';
        copyButton.style.border = 'none';
        copyButton.style.borderRadius = '3px';
        copyButton.style.backgroundColor = '#e8e8e8';
        copyButton.style.cursor = 'pointer';
        
        copyButton.addEventListener('click', function(e) {
          e.preventDefault();
          e.stopPropagation();
          
          navigator.clipboard.writeText(path);
          
          // Efecto visual de confirmación
          this.textContent = '¡Copiado!';
          this.style.backgroundColor = '#49cc90';
          this.style.color = 'white';
          
          setTimeout(() => {
            this.textContent = 'Copiar ruta';
            this.style.backgroundColor = '#e8e8e8';
            this.style.color = 'inherit';
          }, 1500);
        });
        
        summary.appendChild(copyButton);
      }
    }
  });
}

/**
 * Añade una barra de navegación rápida para saltar entre secciones
 */
function addQuickNavigation() {
  // Obtener todas las etiquetas (secciones)
  const tags = document.querySelectorAll('.opblock-tag');
  if (tags.length === 0) return;
  
  // Crear barra de navegación
  const navBar = document.createElement('div');
  navBar.className = 'quick-nav-bar';
  navBar.style.position = 'sticky';
  navBar.style.top = '0';
  navBar.style.backgroundColor = '#f8f8f8';
  navBar.style.padding = '10px 30px';
  navBar.style.marginBottom = '20px';
  navBar.style.borderBottom = '1px solid #e8e8e8';
  navBar.style.zIndex = '1000';
  navBar.style.display = 'flex';
  navBar.style.flexWrap = 'wrap';
  navBar.style.gap = '10px';
  
  // Título de la barra
  const navTitle = document.createElement('span');
  navTitle.textContent = 'Navegación Rápida: ';
  navTitle.style.fontWeight = 'bold';
  navTitle.style.marginRight = '10px';
  navBar.appendChild(navTitle);
  
  // Añadir enlaces para cada sección
  tags.forEach(tag => {
    const tagId = tag.getAttribute('id');
    const tagName = tag.querySelector('a.nostyle').innerText;
    
    const navLink = document.createElement('a');
    navLink.href = `#${tagId}`;
    navLink.textContent = tagName.replace(/\s*\d+\s*/, '').trim(); // Eliminar números de prefijo
    navLink.style.display = 'inline-block';
    navLink.style.padding = '3px 8px';
    navLink.style.backgroundColor = '#e8e8e8';
    navLink.style.borderRadius = '3px';
    navLink.style.textDecoration = 'none';
    navLink.style.color = '#333';
    navLink.style.fontSize = '12px';
    
    navLink.addEventListener('mouseenter', function() {
      this.style.backgroundColor = '#3b7ea1';
      this.style.color = 'white';
    });
    
    navLink.addEventListener('mouseleave', function() {
      this.style.backgroundColor = '#e8e8e8';
      this.style.color = '#333';
    });
    
    navBar.appendChild(navLink);
  });
  
  // Insertar la barra después del contenedor de esquemas
  const schemeContainer = document.querySelector('.scheme-container');
  if (schemeContainer && schemeContainer.parentNode) {
    schemeContainer.parentNode.insertBefore(navBar, schemeContainer.nextSibling);
  } else {
    // Alternativa: insertar después de la información
    const info = document.querySelector('.info');
    if (info && info.parentNode) {
      info.parentNode.insertBefore(navBar, info.nextSibling);
    }
  }
}

/**
 * Mejora la función de búsqueda
 */
function enhanceSearch() {
  const operations = document.querySelectorAll('.opblock');
  if (operations.length === 0) return;
  
  // Crear contenedor de búsqueda
  const searchContainer = document.createElement('div');
  searchContainer.className = 'enhanced-search-container';
  searchContainer.style.padding = '15px 30px';
  searchContainer.style.backgroundColor = 'white';
  searchContainer.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)';
  searchContainer.style.borderRadius = '5px';
  searchContainer.style.margin = '0 0 20px 0';
  
  // Crear campo de búsqueda
  const searchInput = document.createElement('input');
  searchInput.type = 'text';
  searchInput.placeholder = 'Buscar endpoint por ruta, método o descripción...';
  searchInput.style.width = '100%';
  searchInput.style.padding = '10px 15px';
  searchInput.style.border = '1px solid #e8e8e8';
  searchInput.style.borderRadius = '4px';
  searchInput.style.fontSize = '14px';
  
  searchContainer.appendChild(searchInput);
  
  // Filtros adicionales
  const filterContainer = document.createElement('div');
  filterContainer.style.display = 'flex';
  filterContainer.style.marginTop = '10px';
  filterContainer.style.gap = '15px';
  
  // Filtro por método HTTP
  const methodFilter = document.createElement('select');
  methodFilter.innerHTML = `
    <option value="">Todos los métodos</option>
    <option value="get">GET</option>
    <option value="post">POST</option>
    <option value="put">PUT</option>
    <option value="delete">DELETE</option>
    <option value="patch">PATCH</option>
  `;
  methodFilter.style.padding = '5px 10px';
  methodFilter.style.borderRadius = '4px';
  methodFilter.style.border = '1px solid #e8e8e8';
  
  // Filtro por módulo
  const moduleFilter = document.createElement('select');
  moduleFilter.innerHTML = '<option value="">Todos los módulos</option>';
  
  // Añadir opciones de módulos dinámicamente
  const modules = new Set();
  document.querySelectorAll('.opblock-tag').forEach(tag => {
    const tagName = tag.querySelector('a.nostyle').innerText;
    const cleanName = tagName.replace(/\s*\d+\s*/, '').trim();
    modules.add(cleanName);
  });
  
  modules.forEach(module => {
    const option = document.createElement('option');
    option.value = module.toLowerCase();
    option.textContent = module;
    moduleFilter.appendChild(option);
  });
  
  moduleFilter.style.padding = '5px 10px';
  moduleFilter.style.borderRadius = '4px';
  moduleFilter.style.border = '1px solid #e8e8e8';
  
  // Etiquetas para los filtros
  const methodLabel = document.createElement('label');
  methodLabel.textContent = 'Método: ';
  methodLabel.style.display = 'flex';
  methodLabel.style.alignItems = 'center';
  methodLabel.style.gap = '5px';
  methodLabel.appendChild(methodFilter);
  
  const moduleLabel = document.createElement('label');
  moduleLabel.textContent = 'Módulo: ';
  moduleLabel.style.display = 'flex';
  moduleLabel.style.alignItems = 'center';
  moduleLabel.style.gap = '5px';
  moduleLabel.appendChild(moduleFilter);
  
  filterContainer.appendChild(methodLabel);
  filterContainer.appendChild(moduleLabel);
  
  searchContainer.appendChild(filterContainer);
  
  // Función de búsqueda y filtrado
  const performSearch = () => {
    const searchTerm = searchInput.value.toLowerCase();
    const methodSelected = methodFilter.value.toLowerCase();
    const moduleSelected = moduleFilter.value.toLowerCase();
    
    operations.forEach(op => {
      const method = op.className.match(/opblock-(\w+)/)[1].toLowerCase();
      const path = op.querySelector('.opblock-summary-path')?.textContent.toLowerCase() || '';
      const description = op.querySelector('.opblock-summary-description')?.textContent.toLowerCase() || '';
      
      // Encontrar a qué módulo pertenece esta operación
      let module = '';
      let parent = op.parentNode;
      while (parent) {
        if (parent.classList && parent.classList.contains('opblock-tag-section')) {
          const tagElement = parent.querySelector('.opblock-tag a.nostyle');
          if (tagElement) {
            module = tagElement.innerText.replace(/\s*\d+\s*/, '').trim().toLowerCase();
            break;
          }
        }
        parent = parent.parentNode;
      }
      
      // Filtrar por todos los criterios
      const matchesSearch = !searchTerm || path.includes(searchTerm) || description.includes(searchTerm);
      const matchesMethod = !methodSelected || method === methodSelected;
      const matchesModule = !moduleSelected || module === moduleSelected;
      
      if (matchesSearch && matchesMethod && matchesModule) {
        op.style.display = 'block';
        // También mostrar la sección padre
        if (op.parentNode && op.parentNode.classList.contains('opblock-tag-section')) {
          op.parentNode.style.display = 'block';
        }
      } else {
        op.style.display = 'none';
      }
    });
    
    // Ocultar secciones vacías después del filtrado
    document.querySelectorAll('.opblock-tag-section').forEach(section => {
      const visibleOps = section.querySelectorAll('.opblock[style="display: block;"]');
      if (visibleOps.length === 0) {
        section.style.display = 'none';
      } else {
        section.style.display = 'block';
      }
    });
  };
  
  // Añadir event listeners
  searchInput.addEventListener('input', performSearch);
  methodFilter.addEventListener('change', performSearch);
  moduleFilter.addEventListener('change', performSearch);
  
  // Insertar después de la información
  const info = document.querySelector('.info');
  if (info && info.parentNode) {
    info.parentNode.insertBefore(searchContainer, info.nextSibling);
  }
}

/**
 * Añade indicadores visuales de estado
 */
function addStatusIndicators() {
  // Simulamos datos de estado - en producción deberían venir de la API
  const statusData = {
    'Autenticación': 'ONLINE',
    'Catálogos': 'ONLINE',
    'Usuarios': 'ONLINE',
    'Citas': 'ONLINE',
    'Médico': 'ONLINE',
    'Pagos': 'ONLINE',
    'Notificaciones': 'ONLINE',
    'Analytics': 'ONLINE',
    'Chatbot': 'ONLINE',
    'Configuración': 'ONLINE'
  };
  
  // Añadir indicadores a cada sección
  document.querySelectorAll('.opblock-tag').forEach(tag => {
    const tagName = tag.querySelector('a.nostyle').innerText;
    const cleanName = tagName.replace(/\s*\d+\s*/, '').trim();
    
    if (statusData[cleanName]) {
      const status = statusData[cleanName];
      
      const statusIndicator = document.createElement('span');
      statusIndicator.className = `status-indicator status-${status.toLowerCase()}`;
      statusIndicator.textContent = status;
      statusIndicator.style.marginLeft = '10px';
      statusIndicator.style.fontSize = '12px';
      statusIndicator.style.padding = '2px 8px';
      statusIndicator.style.borderRadius = '10px';
      
      switch (status) {
        case 'ONLINE':
          statusIndicator.style.backgroundColor = 'rgba(73, 204, 144, 0.15)';
          statusIndicator.style.color = '#49cc90';
          break;
        case 'DEGRADED':
          statusIndicator.style.backgroundColor = 'rgba(252, 161, 48, 0.15)';
          statusIndicator.style.color = '#fca130';
          break;
        case 'OFFLINE':
          statusIndicator.style.backgroundColor = 'rgba(249, 62, 62, 0.15)';
          statusIndicator.style.color = '#f93e3e';
          break;
        case 'MAINTENANCE':
          statusIndicator.style.backgroundColor = 'rgba(97, 175, 254, 0.15)';
          statusIndicator.style.color = '#61affe';
          break;
        case 'BETA':
          statusIndicator.style.backgroundColor = 'rgba(137, 191, 4, 0.15)';
          statusIndicator.style.color = '#89bf04';
          break;
      }
      
      tag.querySelector('a.nostyle').appendChild(statusIndicator);
    }
  });
}

/**
 * Añade contador de endpoints por sección
 */
function countEndpoints() {
  document.querySelectorAll('.opblock-tag-section').forEach(section => {
    const tagElement = section.querySelector('.opblock-tag');
    if (!tagElement) return;
    
    const endpoints = section.querySelectorAll('.opblock');
    const count = endpoints.length;
    
    const countBadge = document.createElement('span');
    countBadge.className = 'endpoint-count';
    countBadge.textContent = `${count} endpoints`;
    countBadge.style.marginLeft = '10px';
    countBadge.style.fontSize = '12px';
    countBadge.style.padding = '2px 8px';
    countBadge.style.backgroundColor = '#e8e8e8';
    countBadge.style.borderRadius = '10px';
    countBadge.style.color = '#333';
    
    tagElement.appendChild(countBadge);
  });
} 