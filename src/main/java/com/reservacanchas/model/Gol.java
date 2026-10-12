package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_gol")
@Getter
@Setter
public class Gol extends EventoPartido {

    @Column(name = "fecha_limite_atribucion")
    private LocalDateTime fechaLimiteAtribucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anotador_id")
    private ParticipantePartido anotador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asistente_id")
    private ParticipantePartido asistente;

    @jakarta.persistence.OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id")
    private Clip clip;

    public void atribuir(ParticipantePartido anotador, ParticipantePartido asistente) {
        this.anotador = anotador;
        this.asistente = asistente;
    }

    public boolean estaVencido() {
        return fechaLimiteAtribucion != null
                && LocalDateTime.now().isAfter(fechaLimiteAtribucion);
    }

    @Override
    public String descripcion() {
        if (anotador != null) {
            return "Gol de " + anotador.getNombreVisible()
                    + (asistente != null ? " (asistencia de " + asistente.getNombreVisible() + ")" : "");
        }
        return "Gol del equipo " + equipo;
    }

    @Override
    public boolean afectaMarcador() {
        return true;
    }
}
