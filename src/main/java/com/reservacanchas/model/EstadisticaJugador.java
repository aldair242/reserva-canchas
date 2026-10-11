package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "estadisticas_jugador")
@Getter
@Setter
public class EstadisticaJugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int partidosJugados;

    @Column(nullable = false)
    private int goles;

    @Column(nullable = false)
    private int asistencias;

    @Column(nullable = false)
    private int victorias;

    @Column(nullable = false)
    private int empates;

    @Column(nullable = false)
    private int derrotas;

    public double getGolesPorPartido() {
        return 0;
    }

    public double getPorcentajeVictorias() {
        return 0;
    }

    public int getParticipacionesEnGol() {
        return 0;
    }
}
