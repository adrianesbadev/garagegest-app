(() => {
    'use strict';

    /**
     * Sistema de validaciones en tiempo real
     */
    class ValidationManager {
        constructor() {
            this.init();
        }

        init() {
            // Validar campos al perder el foco
            document.addEventListener('blur', (e) => {
                if (e.target.matches('input[type="email"], input[type="tel"], input[id="nif"], input[id="matricula"]')) {
                    this.validateField(e.target);
                }
            }, true);

            // Validar campos mientras se escribe (debounce)
            let timeout;
            document.addEventListener('input', (e) => {
                if (e.target.matches('input[type="email"], input[type="tel"], input[id="nif"], input[id="matricula"]')) {
                    clearTimeout(timeout);
                    timeout = setTimeout(() => {
                        this.validateField(e.target);
                    }, 500);
                }
            }, true);

            // Validar formulario antes de enviar
            document.addEventListener('submit', (e) => {
                const form = e.target;
                if (form.tagName === 'FORM') {
                    if (!this.validateForm(form)) {
                        e.preventDefault();
                        e.stopPropagation();
                    }
                }
            }, true);
        }

        validateForm(form) {
            let isValid = true;
            const fields = form.querySelectorAll('input[type="email"], input[type="tel"], input[id="nif"], input[id="matricula"]');

            fields.forEach(field => {
                if (!this.validateField(field)) {
                    isValid = false;
                }
            });

            return isValid;
        }

        validateField(field) {
            const value = field.value.trim();
            const fieldId = field.id;
            let isValid = true;
            let errorMessage = '';

            // Remover estado previo
            this.clearFieldError(field);

            // Validar según el tipo de campo
            if (field.type === 'email' && value) {
                isValid = this.validateEmail(value);
                if (!isValid) {
                    errorMessage = 'El formato del email no es válido';
                }
            } else if (field.type === 'tel' && value) {
                isValid = this.validateTelefono(value);
                if (!isValid) {
                    errorMessage = 'El teléfono debe ser un número español de 9 dígitos (6, 7, 8 o 9)';
                }
            } else if (fieldId === 'nif' && value) {
                isValid = this.validateNif(value);
                if (!isValid) {
                    errorMessage = 'El NIF/NIE no es válido';
                }
            } else if (fieldId === 'matricula' && value) {
                isValid = this.validateMatricula(value);
                if (!isValid) {
                    errorMessage = 'La matrícula no es válida. Formatos: 1234ABC (nuevo) o M1234AB (antiguo)';
                }
            }

            // Mostrar error si es inválido
            if (!isValid && value) {
                this.showFieldError(field, errorMessage);
            } else if (isValid && value) {
                this.showFieldSuccess(field);
            }

            return isValid;
        }

        validateEmail(email) {
            const pattern = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;
            return email.length <= 100 && pattern.test(email);
        }

        validateTelefono(telefono) {
            // Normalizar: quitar espacios, guiones, paréntesis y prefijos
            const normalized = telefono
                .replace(/\s+/g, '')
                .replace(/-/g, '')
                .replace(/\(/g, '')
                .replace(/\)/g, '')
                .replace(/\+34/g, '')
                .replace(/^0034/g, '');

            // Debe tener exactamente 9 dígitos
            if (!/^[0-9]{9}$/.test(normalized)) {
                return false;
            }

            // El primer dígito debe ser 6, 7, 8 o 9
            const primerDigito = normalized.charAt(0);
            return primerDigito === '6' || primerDigito === '7' || 
                   primerDigito === '8' || primerDigito === '9';
        }

        validateNif(nif) {
            // Normalizar: quitar espacios y convertir a mayúsculas
            const normalized = nif.trim().toUpperCase().replace(/\s+/g, '');

            // Validar formato básico
            if (normalized.length < 8 || normalized.length > 9) {
                return false;
            }

            // Validar DNI (8 dígitos + 1 letra)
            if (/^[0-9]{8}[A-Z]$/.test(normalized)) {
                return this.validarNif(normalized);
            }

            // Validar NIE (X/Y/Z + 7 dígitos + letra) - extranjeros
            if (/^[XYZ][0-9]{7}[A-Z]$/.test(normalized)) {
                return this.validarNie(normalized);
            }

            return false;
        }

        validarNif(nif) {
            try {
                const numero = nif.substring(0, 8);
                const letra = nif.substring(8, 9);
                const letras = 'TRWAGMYFPDXBNJZSQVHLCKE';
                const resto = parseInt(numero) % 23;
                return letras.charAt(resto) === letra.charAt(0);
            } catch (e) {
                return false;
            }
        }

        validarNie(nie) {
            try {
                const letraInicial = nie.substring(0, 1);
                const numero = nie.substring(1, 8);
                const letra = nie.substring(8, 9);

                const numeroNif = letraInicial
                    .replace('X', '0')
                    .replace('Y', '1')
                    .replace('Z', '2') + numero;

                const letras = 'TRWAGMYFPDXBNJZSQVHLCKE';
                const resto = parseInt(numeroNif) % 23;
                return letras.charAt(resto) === letra.charAt(0);
            } catch (e) {
                return false;
            }
        }

        validateMatricula(matricula) {
            // Normalizar: quitar espacios, guiones y convertir a mayúsculas
            const normalized = matricula.trim().toUpperCase()
                .replace(/\s+/g, '')
                .replace(/-/g, '');

            // Formato nuevo (desde septiembre 2000): 4 dígitos + 3 letras (sin vocales ni Q)
            // Ejemplo: 1234ABC, 5678XYZ
            if (/^[0-9]{4}[BCDFGHJKLMNPRSTVWXYZ]{3}$/.test(normalized)) {
                return true;
            }

            // Formato antiguo (1971-2000): 1-2 letras (provincia) + 4 números + 1-2 letras
            // Ejemplos: M1234AB, SE1234A, B1234BC
            if (/^[A-Z]{1,2}[0-9]{4}[A-Z]{1,2}$/.test(normalized)) {
                return true;
            }

            return false;
        }

        showFieldError(field, message) {
            field.classList.add('error');
            field.classList.remove('success');

            // Crear o actualizar mensaje de error
            let errorDiv = field.parentElement.querySelector('.field-error');
            if (!errorDiv) {
                errorDiv = document.createElement('div');
                errorDiv.className = 'field-error';
                field.parentElement.appendChild(errorDiv);
            }

            errorDiv.innerHTML = `
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <path d="M12 8v4M12 16h.01"/>
                </svg>
                <span>${message}</span>
            `;
            errorDiv.style.display = 'flex';
        }

        showFieldSuccess(field) {
            field.classList.add('success');
            field.classList.remove('error');
            this.clearFieldError(field);
        }

        clearFieldError(field) {
            field.classList.remove('error', 'success');
            const errorDiv = field.parentElement.querySelector('.field-error');
            if (errorDiv) {
                errorDiv.style.display = 'none';
            }
        }
    }

    // Inicializar cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            new ValidationManager();
        });
    } else {
        new ValidationManager();
    }
})();
