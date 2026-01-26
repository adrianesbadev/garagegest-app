/**
 * Gestiona los tooltips de los botones de acción en las tablas
 * Detecta si el botón está en la última fila y muestra el tooltip por encima
 */
(() => {
    'use strict';

    /**
     * Inicializa los tooltips de los botones de acción.
     * Detecta si el botón está en la última fila y ajusta la posición del tooltip.
     */
    function initTooltips() {
        const iconButtons = document.querySelectorAll('.icon-button[data-tooltip]');
        
        iconButtons.forEach(button => {
            const row = button.closest('tr');
            if (!row) return;
            
            const table = row.closest('table');
            if (!table) return;
            
            const tbody = table.querySelector('tbody');
            if (!tbody) return;
            
            const rows = Array.from(tbody.querySelectorAll('tr'));
            const isLastRow = rows[rows.length - 1] === row;
            
            // Mostrar tooltip arriba si está en la última fila para evitar que se corte
            if (isLastRow) {
                button.classList.add('tooltip-top');
            }
        });
    }

    // Inicializar cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initTooltips);
    } else {
        initTooltips();
    }

    // Re-inicializar después de cambios dinámicos (p. ej., cuando se añaden filas)
    const observer = new MutationObserver(() => {
        initTooltips();
    });

    // Observar cambios en los tbody de las tablas
    document.querySelectorAll('table tbody').forEach(tbody => {
        observer.observe(tbody, { childList: true, subtree: true });
    });
})();
