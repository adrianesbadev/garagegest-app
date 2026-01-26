package com.adrian.taller_app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.adrian.taller_app.validation.MatriculaEspanola;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "VEHICULO")
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "La matrícula es obligatoria")
    @MatriculaEspanola
    @Size(max = 20)
    @Column(name = "matricula", nullable = false, unique = true, length = 20)
    private String matricula;

    @Size(max = 50)
    @Column(name = "marca", length = 50)
    private String marca;

    @Size(max = 50)
    @Column(name = "modelo", length = 50)
    private String modelo;

    @Min(1900)
    @Max(2100)
    @Column(name = "anio")
    private Integer anio;

    @Min(0)
    @Column(name = "km_actual")
    private Integer kmActual;
}
