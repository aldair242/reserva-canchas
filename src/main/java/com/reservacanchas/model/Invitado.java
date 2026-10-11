package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    public void reclamarPerfil(Jugador j) {
        this.reclamado = true;
    }

    @Override
    public String getNombreVisible() {
        return "Invitado " + getNombreCompleto();
    }
}
