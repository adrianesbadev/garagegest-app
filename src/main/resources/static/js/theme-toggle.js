(() => {
    'use strict';

    const THEME_KEY = 'garagegest-theme';
    const DARK_THEME = 'dark';
    const LIGHT_THEME = 'light';

    /**
     * Obtiene el tema actual (desde localStorage o preferencia del sistema)
     */
    function getStoredTheme() {
        return localStorage.getItem(THEME_KEY);
    }

    /**
     * Guarda el tema en localStorage
     */
    function setStoredTheme(theme) {
        localStorage.setItem(THEME_KEY, theme);
    }

    /**
     * Detecta la preferencia del sistema operativo
     */
    function getPreferredTheme() {
        const storedTheme = getStoredTheme();
        if (storedTheme) {
            return storedTheme;
        }

        return window.matchMedia('(prefers-color-scheme: dark)').matches ? DARK_THEME : LIGHT_THEME;
    }

    /**
     * Aplica el tema al documento
     */
    function setTheme(theme) {
        if (theme === DARK_THEME) {
            document.documentElement.setAttribute('data-theme', DARK_THEME);
        } else {
            document.documentElement.removeAttribute('data-theme');
        }
    }

    /**
     * Alterna entre tema claro y oscuro
     */
    function toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === DARK_THEME ? LIGHT_THEME : DARK_THEME;

        setTheme(newTheme);
        setStoredTheme(newTheme);

        // Añadir animación de pulso al botón
        const button = document.querySelector('.theme-toggle');
        if (button) {
            button.style.animation = 'none';
            setTimeout(() => {
                button.style.animation = '';
            }, 10);
        }
    }

    /**
     * Inicializa el tema al cargar la página
     */
    function initTheme() {
        const theme = getPreferredTheme();
        setTheme(theme);
    }

    /**
     * Escucha cambios en la preferencia del sistema
     */
    function watchSystemTheme() {
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
            const storedTheme = getStoredTheme();
            if (!storedTheme) {
                setTheme(e.matches ? DARK_THEME : LIGHT_THEME);
            }
        });
    }

    // Aplicar el tema inmediatamente para evitar flash
    initTheme();

    // Cuando el DOM esté listo
    document.addEventListener('DOMContentLoaded', () => {
        const toggleButton = document.querySelector('.theme-toggle');

        if (toggleButton) {
            toggleButton.addEventListener('click', toggleTheme);
        }

        watchSystemTheme();
    });
})();
