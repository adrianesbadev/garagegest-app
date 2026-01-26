/**
 * Gestiona el toggle de mostrar/ocultar contraseña en el formulario de login.
 */
(() => {
    'use strict';

    const toggles = document.querySelectorAll("[data-toggle-password]");
    toggles.forEach((button) => {
        const targetId = button.getAttribute("data-toggle-password");
        const input = document.getElementById(targetId);
        if (!input) {
            return;
        }
        button.addEventListener("click", () => {
            const isVisible = input.type === "text";
            input.type = isVisible ? "password" : "text";
            button.classList.toggle("is-visible", !isVisible);
            button.setAttribute("aria-pressed", String(!isVisible));
            const label = !isVisible ? "Ocultar contraseña" : "Mostrar contraseña";
            button.setAttribute("aria-label", label);
            const srOnly = button.querySelector(".sr-only");
            if (srOnly) {
                srOnly.textContent = label;
            }
        });
    });
})();
