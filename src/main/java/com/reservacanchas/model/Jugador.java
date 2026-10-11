package com.reservacanchas.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "jugadores")
@Getter
@Setter
public class Jugador extends Usuario {

    @Column(length = 50)
    private String apodo;

    @Column(length = 500)
    private String fotoUrl;

    @Column(length = 50)
    private String posicionPreferida;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "estadistica_id")
    private EstadisticaJugador estadistica;

    public Reserva reservar(Cancha c, LocalDate f, LocalTime ini, LocalTime fin) {
        return null;
    }

    public EstadisticaJugador obtenerEstadisticas() {
        return estadistica;
    }

    @Override
    public String getNombreVisible() {
        return apodo != null && !apodo.isBlank() ? apodo : getNombreCompleto();
    }
}
