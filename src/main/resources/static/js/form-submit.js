/**
 * Evita envíos dobles: deshabilita botones de submit al enviar el formulario.
 * Se aplica a todos los formularios de la aplicación.
 */
(function () {
    'use strict';

    /**
     * Solo deshabilita si el envío no fue cancelado (p. ej. por un diálogo de confirmación).
     * Usamos setTimeout(0) para ejecutar después del resto de listeners; si alguno hizo
     * preventDefault(), defaultPrevented será true y no deshabilitamos.
     */
    function init() {
        document.querySelectorAll('form').forEach(function (form) {
            form.addEventListener('submit', function (e) {
                var buttons = form.querySelectorAll('button[type="submit"], input[type="submit"]');
                var ev = e;
                window.setTimeout(function () {
                    if (ev.defaultPrevented) return;
                    buttons.forEach(function (btn) {
                        btn.disabled = true;
                        if (btn.tagName === 'BUTTON') {
                            btn.setAttribute('aria-busy', 'true');
                        }
                    });
                }, 0);
            });
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
