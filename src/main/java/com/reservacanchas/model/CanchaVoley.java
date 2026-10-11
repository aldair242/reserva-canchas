package com.reservacanchas.model;

import com.reservacanchas.model.enums.TipoDeporte;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "canchas_voley")
@DiscriminatorValue("VOLEY")
@Getter
@Setter
public class CanchaVoley extends Cancha {

    @Column(name = "altura_red")
    private double alturaRed;

    @Column(name = "es_de_playa", nullable = false)
    private boolean esDePlaya;

    @Override
    public TipoDeporte getDeporte() {
        return TipoDeporte.VOLEY;
    }

    @Override
    public int getCapacidadJugadores() {
        return esDePlaya ? 4 : 12;
    }
}
