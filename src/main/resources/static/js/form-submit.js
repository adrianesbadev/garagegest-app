/**
 * Evita envíos dobles: deshabilita botones de submit al enviar el formulario.
 * Se aplica a todos los formularios de la aplicación.
 */
(function () {
    'use strict';

    function init() {
        document.querySelectorAll('form').forEach(function (form) {
            form.addEventListener('submit', function () {
                var buttons = form.querySelectorAll('button[type="submit"], input[type="submit"]');
                buttons.forEach(function (btn) {
                    btn.disabled = true;
                    if (btn.tagName === 'BUTTON') {
                        btn.setAttribute('aria-busy', 'true');
                    }
                });
            });
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
