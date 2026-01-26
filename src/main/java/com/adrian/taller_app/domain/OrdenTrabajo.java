package com.adrian.taller_app.domain;

import com.adrian.taller_app.domain.converter.EstadoOrdenTrabajoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Entity
@Table(name = "ORDEN_TRABAJO")
@Getter
@Setter
@NoArgsConstructor
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ot")
    private Long idOt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_asignado")
    private Usuario usuarioAsignado;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Convert(converter = EstadoOrdenTrabajoConverter.class)
    @Column(name = "estado", length = 20)
    private EstadoOrdenTrabajo estado;

    @Min(0)
    @Column(name = "km_entrada")
    private Integer kmEntrada;

    @Size(max = 2000)
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @DecimalMin("0.0")
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @DecimalMin("0.0")
    @Column(name = "iva_total", precision = 10, scale = 2)
    private BigDecimal ivaTotal;

    @DecimalMin("0.0")
    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    @Transient
    public String getFechaCreacionFormateada() {
        return formatDateTime(fechaCreacion);
    }

    @Transient
    public String getFechaCierreFormateada() {
        return formatDateTime(fechaCierre);
    }

    @Transient
    public String getSubtotalFormateado() {
        return formatCurrency(subtotal);
    }

    @Transient
    public String getIvaTotalFormateado() {
        return formatCurrency(ivaTotal);
    }

    @Transient
    public String getTotalFormateado() {
        return formatCurrency(total);
    }

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return value.format(formatter);
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null) {
            return "-";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(value) + " €";
    }
}
