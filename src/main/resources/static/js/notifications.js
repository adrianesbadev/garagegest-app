(() => {
    'use strict';

    /**
     * Sistema de notificaciones toast
     */
    class ToastManager {
        constructor() {
            this.container = null;
            this.init();
        }

        init() {
            // Crear contenedor si no existe
            if (!document.querySelector('.toast-container')) {
                this.container = document.createElement('div');
                this.container.className = 'toast-container';
                document.body.appendChild(this.container);
            } else {
                this.container = document.querySelector('.toast-container');
            }
        }

        show(message, type = 'success', title = null, duration = 4000) {
            const toast = this.createToast(message, type, title);
            this.container.appendChild(toast);

            // Mostrar con animación
            requestAnimationFrame(() => {
                requestAnimationFrame(() => {
                    toast.classList.add('show');
                });
            });

            // Auto-cerrar
            if (duration > 0) {
                setTimeout(() => this.hide(toast), duration);
            }

            return toast;
        }

        createToast(message, type, title) {
            const toast = document.createElement('div');
            toast.className = `toast ${type}`;

            const icon = this.getIcon(type);
            const defaultTitle = this.getDefaultTitle(type);

            toast.innerHTML = `
                <div class="toast-icon">${icon}</div>
                <div class="toast-content">
                    <div class="toast-title">${title || defaultTitle}</div>
                    <div class="toast-message">${message}</div>
                </div>
                <button class="toast-close" aria-label="Cerrar">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 6L6 18M6 6l12 12"/>
                    </svg>
                </button>
            `;

            // Evento de cerrar
            const closeBtn = toast.querySelector('.toast-close');
            closeBtn.addEventListener('click', () => this.hide(toast));

            return toast;
        }

        hide(toast) {
            toast.classList.remove('show');
            toast.classList.add('hiding');

            setTimeout(() => {
                if (toast.parentElement) {
                    toast.parentElement.removeChild(toast);
                }
            }, 400);
        }

        getIcon(type) {
            const icons = {
                success: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><path d="M22 4L12 14.01l-3-3"/></svg>',
                error: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M15 9l-6 6M9 9l6 6"/></svg>',
                info: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/></svg>',
                warning: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><path d="M12 9v4M12 17h.01"/></svg>'
            };
            return icons[type] || icons.info;
        }

        getDefaultTitle(type) {
            const titles = {
                success: 'Operación exitosa',
                error: 'Error',
                info: 'Información',
                warning: 'Advertencia'
            };
            return titles[type] || 'Notificación';
        }
    }

    /**
     * Sistema de modales de confirmación
     */
    class ModalManager {
        constructor() {
            this.modal = null;
            this.init();
        }

        init() {
            // Crear modal si no existe
            if (!document.querySelector('.modal-overlay')) {
                this.modal = this.createModal();
                document.body.appendChild(this.modal);
            } else {
                this.modal = document.querySelector('.modal-overlay');
            }

            // Cerrar al hacer click fuera
            this.modal.addEventListener('click', (e) => {
                if (e.target === this.modal) {
                    this.hide();
                }
            });

            // Cerrar con ESC
            document.addEventListener('keydown', (e) => {
                if (e.key === 'Escape' && this.modal.classList.contains('active')) {
                    this.hide();
                }
            });
        }

        createModal() {
            const overlay = document.createElement('div');
            overlay.className = 'modal-overlay';
            overlay.innerHTML = `
                <div class="modal-dialog">
                    <div class="modal-header">
                        <div class="modal-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"/>
                                <path d="M15 9l-6 6M9 9l6 6"/>
                            </svg>
                        </div>
                        <div class="modal-content">
                            <h3 class="modal-title">¿Confirmar eliminación?</h3>
                            <p class="modal-message">Esta acción no se puede deshacer.</p>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="button" data-action="cancel">Cancelar</button>
                        <button class="button danger" data-action="confirm">Eliminar</button>
                    </div>
                </div>
            `;
            return overlay;
        }

        confirm(title, message) {
            return new Promise((resolve) => {
                // Actualizar contenido
                this.modal.querySelector('.modal-title').textContent = title;
                this.modal.querySelector('.modal-message').textContent = message;

                // Mostrar modal
                this.show();

                // Manejar botones
                const handleClick = (e) => {
                    const action = e.target.dataset.action;
                    if (action === 'confirm') {
                        resolve(true);
                        this.hide();
                    } else if (action === 'cancel') {
                        resolve(false);
                        this.hide();
                    }
                    this.modal.querySelector('.modal-footer').removeEventListener('click', handleClick);
                };

                this.modal.querySelector('.modal-footer').addEventListener('click', handleClick);
            });
        }

        show() {
            this.modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }

        hide() {
            this.modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    }

    // Crear instancias globales
    window.toastManager = new ToastManager();
    window.modalManager = new ModalManager();

    // Helper functions globales
    window.showToast = (message, type = 'success', title = null, duration = 4000) => {
        return window.toastManager.show(message, type, title, duration);
    };

    window.confirmDelete = (title, message) => {
        return window.modalManager.confirm(title, message);
    };

    // Auto-mostrar toasts desde flash messages
    document.addEventListener('DOMContentLoaded', () => {
        // Buscar mensajes flash success/error
        const successMsg = document.querySelector('.flash.success');
        const errorMsg = document.querySelector('.flash.error');

        if (successMsg && successMsg.textContent.trim()) {
            showToast(successMsg.textContent.trim(), 'success');
            successMsg.style.display = 'none';
        }

        if (errorMsg && errorMsg.textContent.trim()) {
            showToast(errorMsg.textContent.trim(), 'error');
            errorMsg.style.display = 'none';
        }
    });
})();
