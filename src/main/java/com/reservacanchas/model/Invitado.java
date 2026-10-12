package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "invitados")
@Getter
@Setter
public class Invitado extends Persona {

    @Column(length = 100)
    private String tokenReclamo;

    @Column(nullable = false)
    private boolean reclamado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reclamado_por_id")
    private Jugador reclamadoPor;

    public void reclamarPerfil(Jugador j) {
        this.reclamado = true;
        this.reclamadoPor = j;
    }

    @Override
    public String getNombreVisible() {
        return "Invitado " + getNombreCompleto();
    }
}
