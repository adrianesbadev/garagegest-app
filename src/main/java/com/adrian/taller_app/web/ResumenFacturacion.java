package com.adrian.taller_app.web;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * DTO para el resumen de facturación del dashboard
 */
public class ResumenFacturacion {
    private final BigDecimal totalFacturado;
    private final BigDecimal totalPendiente;
    private final long numeroFacturas;
    private final BigDecimal ticketMedio;

    public ResumenFacturacion(BigDecimal totalFacturado, BigDecimal totalPendiente, 
                             long numeroFacturas, BigDecimal ticketMedio) {
        this.totalFacturado = totalFacturado != null ? totalFacturado : BigDecimal.ZERO;
        this.totalPendiente = totalPendiente != null ? totalPendiente : BigDecimal.ZERO;
        this.numeroFacturas = numeroFacturas;
        this.ticketMedio = ticketMedio != null ? ticketMedio : BigDecimal.ZERO;
    }

    public BigDecimal getTotalFacturado() {
        return totalFacturado;
    }

    public BigDecimal getTotalPendiente() {
        return totalPendiente;
    }

    public long getNumeroFacturas() {
        return numeroFacturas;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public String getTotalFacturadoFormateado() {
        return formatCurrency(totalFacturado);
    }

    public String getTotalPendienteFormateado() {
        return formatCurrency(totalPendiente);
    }

    public String getTicketMedioFormateado() {
        return formatCurrency(ticketMedio);
    }

    private String formatCurrency(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(safe) + " €";
    }
}
