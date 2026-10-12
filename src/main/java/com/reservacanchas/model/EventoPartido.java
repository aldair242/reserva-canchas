package com.reservacanchas.model;

import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.OrigenEvento;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_partido")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_evento")
@Getter
@Setter
public abstract class EventoPartido implements IDescartable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    protected LadoEquipo equipo;

    @Column(nullable = false)
    protected LocalDateTime instante;

    @Column(nullable = false)
    protected int minuto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected OrigenEvento origen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected EstadoEvento estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    protected Partido partido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atribuidor_id")
    protected Jugador atribuidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmador_id")
    protected Jugador confirmador;

    public abstract String descripcion();

    public abstract boolean afectaMarcador();

    public void confirmar(Jugador confirmador) {
        this.estado = EstadoEvento.CONFIRMADO;
        this.confirmador = confirmador;
    }

    @Override
    public void descartar() {
        this.estado = EstadoEvento.DESCARTADO;
    }

    @Override
    public boolean estaDescartado() {
        return this.estado == EstadoEvento.DESCARTADO;
    }

    public int calcularMinuto(LocalDateTime inicioReal) {
        if (inicioReal == null || instante == null) {
            return 0;
        }
        long minutos = Duration.between(inicioReal, instante).toMinutes();
        return (int) Math.max(0, minutos);
    }
}
