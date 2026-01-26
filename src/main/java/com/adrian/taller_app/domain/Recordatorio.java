package com.adrian.taller_app.domain;

import com.adrian.taller_app.domain.converter.ModoRecordatorioConverter;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "RECORDATORIO")
@Getter
@Setter
@NoArgsConstructor
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recordatorio")
    private Long idRecordatorio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @Size(max = 50)
    @Column(name = "tipo", length = 50)
    private String tipo;

    @Convert(converter = ModoRecordatorioConverter.class)
    @Column(name = "modo", length = 20)
    private ModoRecordatorio modo;

    @Column(name = "fecha_objetivo")
    private LocalDate fechaObjetivo;

    @Min(0)
    @Column(name = "km_objetivo")
    private Integer kmObjetivo;

    @Size(max = 30)
    @Column(name = "estado", length = 30)
    private String estado;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Transient
    public String getFechaObjetivoFormateada() {
        if (fechaObjetivo == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fechaObjetivo.format(formatter);
    }

    @Transient
    public String getCreadoEnFormateado() {
        if (creadoEn == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return creadoEn.format(formatter);
    }
}
