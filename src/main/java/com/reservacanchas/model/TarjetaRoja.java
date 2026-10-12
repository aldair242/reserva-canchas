package com.reservacanchas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "eventos_tarjeta_roja")
public class TarjetaRoja extends Tarjeta {

    @Override
    public String descripcion() {
        return "Tarjeta roja para " + jugador.getNombreVisible()
                + (motivo != null && !motivo.isBlank() ? ": " + motivo : "");
    }

    public boolean generaExpulsion() {
        return true;
    }
}
