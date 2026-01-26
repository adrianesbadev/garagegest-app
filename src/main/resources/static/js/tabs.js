/**
 * Sistema de pestañas (tabs) para navegación entre secciones.
 * Soporta múltiples grupos de tabs independientes en la misma página.
 */
(() => {
    'use strict';

    const tabsList = document.querySelectorAll("[data-tabs]");
    tabsList.forEach((tabs) => {
        const key = tabs.dataset.tabs;
        const panelsContainer = document.querySelector(`[data-tab-panels="${key}"]`);
        if (!panelsContainer) {
            return;
        }
        const buttons = tabs.querySelectorAll("[data-tab-target]");
        const panels = panelsContainer.querySelectorAll(".tab-panel");

        const activate = (id) => {
            buttons.forEach((btn) => {
                btn.classList.toggle("active", btn.dataset.tabTarget === id);
            });
            panels.forEach((panel) => {
                panel.classList.toggle("active", panel.id === id);
            });
        };

        const initial = buttons[0]?.dataset.tabTarget;
        if (initial) {
            activate(initial);
        }

        buttons.forEach((btn) => {
            btn.addEventListener("click", () => activate(btn.dataset.tabTarget));
        });
    });
})();
