package com.reservacanchas.model;

import com.reservacanchas.model.enums.TipoDeporte;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "canchas_padel")
@DiscriminatorValue("PADEL")
@Getter
@Setter
public class CanchaPadel extends Cancha {

    @Column(name = "paredes_de_vidrio", nullable = false)
    private boolean paredesDeVidrio;

    @Override
    public TipoDeporte getDeporte() {
        return TipoDeporte.PADEL;
    }

    @Override
    public int getCapacidadJugadores() {
        return 4;
    }
}
