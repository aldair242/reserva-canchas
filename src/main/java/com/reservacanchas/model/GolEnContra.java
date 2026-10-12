package com.reservacanchas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventos_gol_en_contra")
@Getter
@Setter
public class GolEnContra extends EventoPartido {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private ParticipantePartido autor;

    @Override
    public String descripcion() {
        return "Gol en contra de " + autor.getNombreVisible();
    }

    @Override
    public boolean afectaMarcador() {
        return true;
    }
}
