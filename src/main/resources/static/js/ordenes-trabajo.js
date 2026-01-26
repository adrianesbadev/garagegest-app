/**
 * Calcula automáticamente IVA (21%) y total basado en el subtotal
 * en el formulario de órdenes de trabajo.
 */
(() => {
    'use strict';

    const subtotalInput = document.getElementById("subtotal");
    const ivaInput = document.getElementById("ivaTotal");
    const totalInput = document.getElementById("total");

    if (!subtotalInput || !ivaInput || !totalInput) {
        return;
    }

    const parseValue = (input) => {
        if (!input.value) {
            return null;
        }
        const parsed = Number(input.value.replace(",", "."));
        return Number.isNaN(parsed) ? null : parsed;
    };

    const formatValue = (value) => {
        return value.toFixed(2);
    };

    const updateFromSubtotal = () => {
        const subtotal = parseValue(subtotalInput);
        if (subtotal === null) {
            ivaInput.value = "";
            totalInput.value = "";
            return;
        }
        const iva = subtotal * 0.21;
        const total = subtotal + iva;
        ivaInput.value = formatValue(iva);
        totalInput.value = formatValue(total);
    };

    subtotalInput.addEventListener("input", updateFromSubtotal);
    updateFromSubtotal();
})();
