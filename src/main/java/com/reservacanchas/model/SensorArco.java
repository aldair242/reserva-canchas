package com.reservacanchas.model;

import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.UbicacionSensor;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensores_arco")
@DiscriminatorValue("SENSOR_ARCO")
@Getter
@Setter
public class SensorArco extends Dispositivo {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UbicacionSensor ubicacion;

    @Column(nullable = false)
    private int sensibilidad = 50;

    public LocalDateTime emitirSenal() {
        registrarLatido();
        return LocalDateTime.now();
    }

    @Override
    public String getTipo() {
        return "SENSOR_ARCO";
    }

    /**
     * Deduce el equipo que anota el gol a partir del arco donde se encuentra el sensor.
     * Si el balón entra al arco A (defendido por el equipo A), el gol lo anotó el equipo B.
     */
    public LadoEquipo deducirEquipoAnotador() {
        return this.ubicacion == UbicacionSensor.ARCO_A ? LadoEquipo.B : LadoEquipo.A;
    }
}

