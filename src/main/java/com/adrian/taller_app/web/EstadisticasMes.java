package com.adrian.taller_app.web;

/**
 * DTO para estadísticas mensuales del dashboard
 */
public class EstadisticasMes {
    private final int mes;
    private final int anio;
    private final long cantidad;

    public EstadisticasMes(int mes, int anio, long cantidad) {
        this.mes = mes;
        this.anio = anio;
        this.cantidad = cantidad;
    }

    public int getMes() {
        return mes;
    }

    public int getAnio() {
        return anio;
    }

    public long getCantidad() {
        return cantidad;
    }

    public String getMesNombre() {
        return switch (mes) {
            case 1 -> "Enero";
            case 2 -> "Febrero";
            case 3 -> "Marzo";
            case 4 -> "Abril";
            case 5 -> "Mayo";
            case 6 -> "Junio";
            case 7 -> "Julio";
            case 8 -> "Agosto";
            case 9 -> "Septiembre";
            case 10 -> "Octubre";
            case 11 -> "Noviembre";
            case 12 -> "Diciembre";
            default -> "";
        };
    }

    public String getMesAbreviado() {
        return switch (mes) {
            case 1 -> "Ene";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Abr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Ago";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dic";
            default -> "";
        };
    }
}
