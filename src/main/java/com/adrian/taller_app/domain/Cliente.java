package com.adrian.taller_app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import com.adrian.taller_app.validation.EmailReal;
import com.adrian.taller_app.validation.Nif;
import com.adrian.taller_app.validation.TelefonoEspanol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CLIENTE")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @TelefonoEspanol(required = false)
    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @EmailReal(required = false)
    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Nif(required = false)
    @Size(max = 20)
    @Column(name = "nif", length = 20, unique = true)
    private String nif;

    @Column(name = "fecha_alta", insertable = false, updatable = false)
    private LocalDateTime fechaAlta;

    @OneToMany(mappedBy = "cliente")
    private List<Vehiculo> vehiculos = new ArrayList<>();

    @Transient
    public String getFechaAltaFormateada() {
        if (fechaAlta == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaAlta.format(formatter);
    }
}
