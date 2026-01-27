/**
 * Password Strength Indicator
 * Muestra una barra de colores que indica la fortaleza de la contraseña
 */
console.log('[Password Strength] Script loaded');

(function() {
    'use strict';

    /**
     * Calcula la fortaleza de la contraseña
     * @param {string} password - La contraseña a evaluar
     * @returns {Object} Objeto con score (0-4) y mensaje
     */
    function calculateStrength(password) {
        if (!password || password.length === 0) {
            return { score: 0, message: '', percentage: 0 };
        }

        let score = 0;
        const checks = {
            length: password.length >= 8,
            lengthLong: password.length >= 12,
            lowercase: /[a-z]/.test(password),
            uppercase: /[A-Z]/.test(password),
            numbers: /[0-9]/.test(password),
            special: /[^a-zA-Z0-9]/.test(password)
        };

        // Puntuación basada en criterios
        if (checks.length) score += 1;
        if (checks.lengthLong) score += 1;
        if (checks.lowercase) score += 1;
        if (checks.uppercase) score += 1;
        if (checks.numbers) score += 1;
        if (checks.special) score += 1;

        // Normalizar a escala 0-4
        let strength = 0;
        let message = '';
        let percentage = 0;

        if (score <= 2) {
            strength = 1; // Débil
            message = 'Contraseña débil';
            percentage = 25;
        } else if (score === 3 || score === 4) {
            strength = 2; // Regular
            message = 'Contraseña regular';
            percentage = 50;
        } else if (score === 5) {
            strength = 3; // Buena
            message = 'Contraseña buena';
            percentage = 75;
        } else {
            strength = 4; // Muy fuerte
            message = 'Contraseña muy fuerte';
            percentage = 100;
        }

        return { score: strength, message, percentage, checks };
    }

    /**
     * Obtiene el color según la fortaleza
     * @param {number} strength - Nivel de fortaleza (1-4)
     * @returns {string} Color en formato CSS
     */
    function getStrengthColor(strength) {
        const colors = {
            1: '#E02929', // Rojo
            2: '#F9A410', // Naranja
            3: '#F9A410', // Amarillo/Naranja
            4: '#41C885'  // Verde
        };
        return colors[strength] || colors[1];
    }

    /**
     * Inicializa el indicador de fortaleza
     */
    function initPasswordStrength() {
        const passwordInput = document.getElementById('password');
        if (!passwordInput) {
            console.log('[Password Strength] Password input not found');
            return;
        }

        console.log('[Password Strength] Input found, initializing...');

        // Buscar el contenedor del campo (form-field)
        const formField = passwordInput.closest('.form-field');
        if (!formField) {
            console.log('[Password Strength] Form field container not found');
            return;
        }

        console.log('[Password Strength] Form field found');

        // Buscar el contenedor que ya existe en el HTML
        const container = formField.querySelector('.password-strength-container');
        if (!container) {
            console.error('[Password Strength] Container not found in HTML');
            return;
        }
        
        console.log('[Password Strength] Container found in HTML');

        const fillBar = container.querySelector('#password-strength-fill');
        const messageEl = container.querySelector('#password-strength-message');
        const hintsEl = container.querySelector('#password-strength-hints');

        if (!fillBar || !messageEl || !hintsEl) {
            console.error('[Password Strength] Elements not found in container');
            return;
        }

        console.log('[Password Strength] All elements found, setting up listeners');

        /**
         * Actualiza el indicador
         */
        function updateIndicator() {
            const password = passwordInput.value;
            console.log('[Password Strength] Updating indicator, password length:', password.length);
            const result = calculateStrength(password);

            if (password.length === 0) {
                fillBar.style.width = '0%';
                fillBar.style.backgroundColor = 'transparent';
                messageEl.textContent = '';
                hintsEl.innerHTML = '';
                container.style.display = 'none';
                return;
            }

            console.log('[Password Strength] Showing indicator, strength:', result.score);
            container.style.display = 'block';
            fillBar.style.width = result.percentage + '%';
            fillBar.style.backgroundColor = getStrengthColor(result.score);
            messageEl.textContent = result.message;
            messageEl.className = 'password-strength-message strength-' + result.score;

            // Mostrar sugerencias
            const suggestions = [];
            if (!result.checks.length) {
                suggestions.push('Al menos 8 caracteres');
            }
            if (!result.checks.uppercase) {
                suggestions.push('Incluir mayúsculas');
            }
            if (!result.checks.lowercase) {
                suggestions.push('Incluir minúsculas');
            }
            if (!result.checks.numbers) {
                suggestions.push('Incluir números');
            }
            if (!result.checks.special) {
                suggestions.push('Incluir caracteres especiales');
            }

            if (suggestions.length > 0) {
                hintsEl.innerHTML = '<span class="hint-label">Sugerencias:</span> ' + 
                    suggestions.join(', ');
            } else {
                hintsEl.innerHTML = '';
            }
        }

        // Event listeners
        passwordInput.addEventListener('input', updateIndicator);
        passwordInput.addEventListener('focus', updateIndicator);
        passwordInput.addEventListener('blur', function() {
            // Mantener visible incluso al perder el foco si hay texto
            if (passwordInput.value.length > 0) {
                updateIndicator();
            }
        });

        // Inicializar si ya hay valor (en caso de edición)
        if (passwordInput.value) {
            updateIndicator();
        }
    }

    // Inicializar cuando el DOM esté listo
    function initialize() {
        console.log('[Password Strength] Initializing...');
        // Esperar un poco para asegurar que Thymeleaf haya procesado todo
        setTimeout(function() {
            initPasswordStrength();
        }, 500);
    }

    // Esperar a que el DOM esté completamente cargado
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initialize);
    } else {
        // Si el DOM ya está cargado, ejecutar directamente
        initialize();
    }
})();
