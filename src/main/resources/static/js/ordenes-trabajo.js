/**
 * Formulario de órdenes de trabajo:
 * - Calcula IVA (21%) y total según subtotal
 * - Mínimo de km de entrada según el vehículo seleccionado
 */
(() => {
    'use strict';

    const subtotalInput = document.getElementById("subtotal");
    const ivaInput = document.getElementById("ivaTotal");
    const totalInput = document.getElementById("total");
    const vehiculoSelect = document.getElementById("vehiculo");
    const kmEntradaInput = document.getElementById("kmEntrada");

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

    /** Mínimo de km: en edición, no bajar del km original de la orden; en creación, según km actual del vehículo */
    if (vehiculoSelect && kmEntradaInput) {
        const form = kmEntradaInput.closest("form");
        const kmMinOriginal = form && form.hasAttribute("data-km-min-original")
            ? parseInt(form.getAttribute("data-km-min-original"), 10)
            : null;

        const updateKmMin = () => {
            const selected = vehiculoSelect.options[vehiculoSelect.selectedIndex];
            const vehicleKm = selected && selected.hasAttribute("data-km")
                ? parseInt(selected.getAttribute("data-km"), 10)
                : 0;
            const minKm = kmMinOriginal != null
                ? kmMinOriginal
                : vehicleKm;
            kmEntradaInput.min = minKm;
            if (kmEntradaInput.value !== "" && parseInt(kmEntradaInput.value, 10) < minKm) {
                kmEntradaInput.value = minKm;
            }
        };

        vehiculoSelect.addEventListener("change", updateKmMin);
        updateKmMin();
    }
})();
