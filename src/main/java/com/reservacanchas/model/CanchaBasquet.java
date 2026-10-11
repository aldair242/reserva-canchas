package com.reservacanchas.model;

import com.reservacanchas.model.enums.TipoDeporte;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "canchas_basquet")
@DiscriminatorValue("BASQUET")
@Getter
@Setter
public class CanchaBasquet extends Cancha {

    @Column(nullable = false)
    private boolean medioCampo;

    @Override
    public TipoDeporte getDeporte() {
        return TipoDeporte.BASQUET;
    }

    @Override
    public int getCapacidadJugadores() {
        return medioCampo ? 6 : 10;
    }
}
