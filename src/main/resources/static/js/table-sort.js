/**
 * Sistema de ordenamiento por columnas en tablas
 */
(() => {
    'use strict';

    class TableSorter {
        constructor(table) {
            this.table = table;
            this.tbody = table.querySelector('tbody');
            this.rows = Array.from(this.tbody.querySelectorAll('tr:not(.empty-state-row)'));
            this.emptyStateRow = this.tbody.querySelector('tr.empty-state-row');
            this.currentSort = { column: null, direction: 'asc' };
            this.init();
        }

        init() {
            const headers = this.table.querySelectorAll('thead th');
            headers.forEach((header, index) => {
                // Saltar columna de acciones
                if (header.textContent.trim().toLowerCase() === 'acciones' || 
                    header.querySelector('.action-buttons')) {
                    return;
                }

                // Configurar header como clickeable
                header.style.cursor = 'pointer';
                header.style.userSelect = 'none';
                header.setAttribute('role', 'button');
                header.setAttribute('tabindex', '0');
                header.setAttribute('aria-label', `Ordenar por ${header.textContent.trim()}`);

                // Añadir icono visual de ordenamiento
                const sortIcon = document.createElement('span');
                sortIcon.className = 'sort-icon';
                sortIcon.innerHTML = `
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M8 9l4-4 4 4M8 15l4 4 4-4"/>
                    </svg>
                `;
                header.appendChild(sortIcon);

                // Configurar eventos de ordenamiento
                const handleSort = () => {
                    this.sort(index, header);
                };

                header.addEventListener('click', handleSort);
                header.addEventListener('keydown', (e) => {
                    if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        handleSort();
                    }
                });
            });
        }

        sort(columnIndex, header) {
            const direction = this.currentSort.column === columnIndex && 
                             this.currentSort.direction === 'asc' ? 'desc' : 'asc';

            // Actualizar estado interno
            this.currentSort = { column: columnIndex, direction };

            // Resetear indicadores visuales en todos los headers
            const allHeaders = this.table.querySelectorAll('thead th');
            allHeaders.forEach(h => {
                const icon = h.querySelector('.sort-icon');
                if (icon) {
                    icon.classList.remove('sort-asc', 'sort-desc');
                }
            });

            // Actualizar indicador visual del header actual
            const icon = header.querySelector('.sort-icon');
            if (icon) {
                icon.classList.add(direction === 'asc' ? 'sort-asc' : 'sort-desc');
            }

            // Ordenar filas según el tipo de dato (numérico o texto)
            const sortedRows = [...this.rows].sort((a, b) => {
                const aCell = a.cells[columnIndex];
                const bCell = b.cells[columnIndex];

                if (!aCell || !bCell) return 0;

                const aText = aCell.textContent.trim();
                const bText = bCell.textContent.trim();

                // Intentar parsear como número
                const aNum = parseFloat(aText.replace(/[^\d,.-]/g, '').replace(',', '.'));
                const bNum = parseFloat(bText.replace(/[^\d,.-]/g, '').replace(',', '.'));

                let comparison = 0;

                if (!isNaN(aNum) && !isNaN(bNum)) {
                    // Comparación numérica
                    comparison = aNum - bNum;
                } else {
                    // Comparación de texto
                    comparison = aText.localeCompare(bText, 'es', { 
                        numeric: true, 
                        sensitivity: 'base' 
                    });
                }

                return direction === 'asc' ? comparison : -comparison;
            });

            // Aplicar ordenamiento al DOM
            sortedRows.forEach(row => this.tbody.appendChild(row));

            // Ocultar fila de estado vacío durante el ordenamiento
            if (this.emptyStateRow) {
                this.emptyStateRow.style.display = 'none';
            }
        }
    }

    // Inicializar ordenamiento en todas las tablas
    function initTableSorting() {
        const tables = document.querySelectorAll('table.table');
        tables.forEach(table => {
            // Solo si tiene filas (excluyendo empty state)
            const hasRows = table.querySelector('tbody tr:not(.empty-state-row)');
            if (hasRows) {
                new TableSorter(table);
            }
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initTableSorting);
    } else {
        initTableSorting();
    }
})();
