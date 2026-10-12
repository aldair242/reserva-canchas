package com.reservacanchas.model;

import com.reservacanchas.model.enums.EstadoClip;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clips")
@Getter
@Setter
public class Clip implements IDescartable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_video")
    private String urlVideo;

    @Column(name = "url_miniatura")
    private String urlMiniatura;

    @Column(name = "segundos_antes", nullable = false)
    private int segundosAntes = 15;

    @Column(name = "segundos_despues", nullable = false)
    private int segundosDespues = 8;

    @Column(name = "duracion_segundos")
    private int duracionSegundos;

    @Column
    private LocalDateTime instante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoClip estado = EstadoClip.PENDIENTE;

    @Column(nullable = false)
    private boolean visible = true;

    @Column(nullable = false)
    private int vistas = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grabacion_id", nullable = false)
    private Grabacion grabacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    public Intervalo calcularRango() {
        if (instante == null) {
            return null;
        }
        return new Intervalo(
                instante.minusSeconds(segundosAntes),
                instante.plusSeconds(segundosDespues)
        );
    }

    public void marcarListo(String url, String miniatura) {
        this.urlVideo = url;
        this.urlMiniatura = miniatura;
        this.estado = EstadoClip.LISTO;
        this.duracionSegundos = this.segundosAntes + this.segundosDespues;
    }

    public void marcarError(String motivo) {
        this.estado = EstadoClip.ERROR;
    }

    @Override
    public void descartar() {
        this.estado = EstadoClip.DESCARTADO;
        this.visible = false;
    }

    @Override
    public boolean estaDescartado() {
        return this.estado == EstadoClip.DESCARTADO || !this.visible;
    }

    public void incrementarVistas() {
        this.vistas++;
    }
}

