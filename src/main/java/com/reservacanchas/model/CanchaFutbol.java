package com.reservacanchas.model;

import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.TipoDeporte;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "canchas_futbol")
@DiscriminatorValue("FUTBOL")
@Getter
@Setter
public class CanchaFutbol extends Cancha {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormatoFutbol formato;

    @Override
    public TipoDeporte getDeporte() {
        return TipoDeporte.FUTBOL;
    }

    @Override
    public int getCapacidadJugadores() {
        if (formato == null) {
            return 0;
        }
        return switch (formato) {
            case FUTBOL_5 -> 10;
            case FUTBOL_7 -> 14;
            case FUTBOL_11 -> 22;
        };
    }
}
