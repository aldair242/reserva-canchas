package com.reservacanchas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "eventos_tarjeta_amarilla")
public class TarjetaAmarilla extends Tarjeta {

    @Override
    public String descripcion() {
        return "Tarjeta amarilla para " + jugador.getNombreVisible()
                + (motivo != null && !motivo.isBlank() ? ": " + motivo : "");
    }
}
