/**
 * Gestiona la lógica del formulario de recordatorios:
 * - Habilita/deshabilita campos según el modo seleccionado
 * - Establece el mínimo de km basado en los km actuales del vehículo
 */
(() => {
    'use strict';

    const modoSelect = document.getElementById("modo");
    const fechaInput = document.getElementById("fechaObjetivo");
    const kmInput = document.getElementById("kmObjetivo");
    const vehiculoSelect = document.getElementById("vehiculo");
    const kmHint = document.getElementById("kmHint");

    if (!modoSelect || !fechaInput || !kmInput || !vehiculoSelect) {
        return;
    }

    const updateKmMin = () => {
        const option = vehiculoSelect.selectedOptions[0];
        const kmActual = option ? Number(option.dataset.km || 0) : 0;
        kmInput.min = String(kmActual);
        if (kmInput.value && Number(kmInput.value) < kmActual) {
            kmInput.value = String(kmActual);
        }
        if (kmHint) {
            kmHint.textContent = kmActual > 0 ? `Mínimo: ${kmActual} km` : "Mínimo: 0 km";
        }
    };

    const toggleFields = () => {
        const modo = modoSelect.value;
        const porFecha = modo === "POR_FECHA";
        const porKm = modo === "POR_KM";

        fechaInput.disabled = porKm;
        kmInput.disabled = porFecha;
        updateKmMin();
    };

    modoSelect.addEventListener("change", toggleFields);
    vehiculoSelect.addEventListener("change", updateKmMin);
    toggleFields();
})();
