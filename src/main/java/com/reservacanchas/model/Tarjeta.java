package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventos_tarjeta")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Tarjeta extends EventoPartido {

    @Column(length = 255)
    protected String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    protected ParticipantePartido jugador;

    @Override
    public boolean afectaMarcador() {
        return false;
    }
}
