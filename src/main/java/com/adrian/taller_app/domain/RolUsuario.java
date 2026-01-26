package com.adrian.taller_app.domain;

public enum RolUsuario {
    ADMIN("admin"),
    RECEPCION("recepcion"),
    MECANICO("mecanico");

    private final String valor;

    RolUsuario(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static RolUsuario fromValor(String valor) {
        if (valor == null) {
            return null;
        }
        for (RolUsuario rol : values()) {
            if (rol.valor.equalsIgnoreCase(valor)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + valor);
    }
}
