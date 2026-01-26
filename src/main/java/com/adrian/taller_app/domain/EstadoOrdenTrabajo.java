package com.adrian.taller_app.domain;

public enum EstadoOrdenTrabajo {
    ABIERTA("abierta", "Abierta"),
    EN_CURSO("en_curso", "En curso"),
    TERMINADA("terminada", "Terminada"),
    ENTREGADA("entregada", "Entregada");

    private final String valor;
    private final String etiqueta;

    EstadoOrdenTrabajo(String valor, String etiqueta) {
        this.valor = valor;
        this.etiqueta = etiqueta;
    }

    public String getValor() {
        return valor;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoOrdenTrabajo fromValor(String valor) {
        if (valor == null) {
            return null;
        }
        for (EstadoOrdenTrabajo estado : values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de OT no válido: " + valor);
    }
}
