/**
 * Sistema de búsqueda y filtrado en tiempo real para listas
 */
class ListSearch {
    constructor(tableSelector, searchPlaceholder = 'Buscar...') {
        this.table = document.querySelector(tableSelector);
        if (!this.table) return;

        this.tbody = this.table.querySelector('tbody');
        if (!this.tbody) return;

        // Obtener todas las filas excepto la de estado vacío
        const allRows = Array.from(this.tbody.querySelectorAll('tr'));
        this.emptyStateRow = allRows.find(row => row.classList.contains('empty-state-row') || row.querySelector('.empty-state'));
        this.rows = allRows.filter(row => row !== this.emptyStateRow);
        
        this.init(searchPlaceholder);
    }

    init(placeholder) {
        // Crear barra de búsqueda
        const searchContainer = this.createSearchBar(placeholder);
        
        // Insertar antes de la tabla
        const tableCard = this.table.closest('.table-card');
        if (tableCard) {
            const tableWrap = tableCard.querySelector('.table-wrap');
            if (tableWrap) {
                tableCard.insertBefore(searchContainer, tableWrap);
            } else {
                tableCard.insertBefore(searchContainer, this.table);
            }
        } else {
            this.table.parentElement.insertBefore(searchContainer, this.table);
        }

        // Configurar evento de búsqueda
        const searchInput = searchContainer.querySelector('.search-input');
        const resultsCounter = searchContainer.querySelector('.search-results');
        const clearBtn = searchContainer.querySelector('.search-clear');
        
        let searchTimeout;
        const performSearch = (value) => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                this.filterRows(value.trim(), resultsCounter);
            }, 150);
        };

        searchInput.addEventListener('input', (e) => {
            const value = e.target.value;
            clearBtn.style.display = value ? 'flex' : 'none';
            performSearch(value);
        });

        clearBtn.addEventListener('click', () => {
            searchInput.value = '';
            searchInput.focus();
            clearBtn.style.display = 'none';
            this.filterRows('', resultsCounter);
        });

        // Contador inicial
        this.updateCounter(this.rows.length, resultsCounter);
    }

    createSearchBar(placeholder) {
        const container = document.createElement('div');
        container.className = 'search-bar-container';
        container.innerHTML = `
            <div class="search-bar">
                <div class="search-input-wrapper">
                    <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="11" cy="11" r="8"/>
                        <path d="m21 21-4.35-4.35"/>
                    </svg>
                    <input type="text" 
                           class="search-input" 
                           placeholder="${placeholder}"
                           aria-label="Buscar">
                    <button class="search-clear" type="button" aria-label="Limpiar búsqueda" style="display: none;">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <path d="m15 9-6 6M9 9l6 6"/>
                        </svg>
                    </button>
                </div>
                <div class="search-results">0 resultados</div>
            </div>
        `;
        return container;
    }

    filterRows(query, resultsCounter) {
        const lowerQuery = query.toLowerCase();
        let visibleCount = 0;

        this.rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            const matches = !query || text.includes(lowerQuery);
            
            row.style.display = matches ? '' : 'none';
            if (matches) visibleCount++;
        });

        // Mostrar/ocultar fila de estado vacío
        if (this.emptyStateRow) {
            const emptyTd = this.emptyStateRow.querySelector('td');
            if (emptyTd) {
                if (visibleCount === 0 && query) {
                    this.emptyStateRow.style.display = '';
                    const colCount = this.table.querySelector('thead tr').children.length;
                    emptyTd.setAttribute('colspan', colCount);
                } else {
                    this.emptyStateRow.style.display = 'none';
                }
            }
        }

        this.updateCounter(visibleCount, resultsCounter, query);
    }

    updateCounter(count, counter, query = '') {
        if (!counter) return;
        
        if (query && count === 0) {
            counter.textContent = 'No se encontraron resultados';
            counter.classList.add('no-results');
        } else if (query) {
            counter.textContent = `${count} ${count === 1 ? 'resultado' : 'resultados'} encontrado${count === 1 ? '' : 's'}`;
            counter.classList.remove('no-results');
        } else {
            counter.textContent = `${count} ${count === 1 ? 'registro' : 'registros'} en total`;
            counter.classList.remove('no-results');
        }
    }
}
