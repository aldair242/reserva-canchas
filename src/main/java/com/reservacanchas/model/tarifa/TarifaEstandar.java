package com.reservacanchas.model.tarifa;

import com.reservacanchas.model.Cancha;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class TarifaEstandar implements EstrategiaTarifa {

    private final BigDecimal precioHora;

    public TarifaEstandar(BigDecimal precioHora) {
        this.precioHora = precioHora;
    }

    public BigDecimal getPrecioHora() {
        return precioHora;
    }

    @Override
    public BigDecimal calcular(Cancha cancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        long minutos = Duration.between(inicio, fin).toMinutes();
        BigDecimal horas = BigDecimal.valueOf(minutos).divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
        return precioHora.multiply(horas);
    }
}
