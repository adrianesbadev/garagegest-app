package com.adrian.taller_app.domain;

public enum ModoRecordatorio {
    POR_FECHA("por_fecha", "Por fecha"),
    POR_KM("por_km", "Por kilómetros"),
    AMBOS("ambos", "Ambos");

    private final String valor;
    private final String etiqueta;

    ModoRecordatorio(String valor, String etiqueta) {
        this.valor = valor;
        this.etiqueta = etiqueta;
    }

    public String getValor() {
        return valor;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static ModoRecordatorio fromValor(String valor) {
        if (valor == null) {
            return null;
        }
        for (ModoRecordatorio modo : values()) {
            if (modo.valor.equalsIgnoreCase(valor)) {
                return modo;
            }
        }
        throw new IllegalArgumentException("Modo no válido: " + valor);
    }
}
