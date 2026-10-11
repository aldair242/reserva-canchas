package com.reservacanchas.model.tarifa;

import com.reservacanchas.model.Cancha;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TarifaHoraPico implements EstrategiaTarifa {

    private final BigDecimal precioNormal;
    private final BigDecimal precioPico;
    private final LocalTime inicioPico;
    private final LocalTime finPico;

    public TarifaHoraPico(BigDecimal precioNormal, BigDecimal precioPico,
                          LocalTime inicioPico, LocalTime finPico) {
        this.precioNormal = precioNormal;
        this.precioPico = precioPico;
        this.inicioPico = inicioPico;
        this.finPico = finPico;
    }

    public BigDecimal getPrecioNormal() {
        return precioNormal;
    }

    public BigDecimal getPrecioPico() {
        return precioPico;
    }

    public LocalTime getInicioPico() {
        return inicioPico;
    }

    public LocalTime getFinPico() {
        return finPico;
    }

    @Override
    public BigDecimal calcular(Cancha cancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        List<LocalTime> puntos = new ArrayList<>();
        puntos.add(inicio);
        puntos.add(fin);
        if (inicioPico.isAfter(inicio) && inicioPico.isBefore(fin)) {
            puntos.add(inicioPico);
        }
        if (finPico.isAfter(inicio) && finPico.isBefore(fin)) {
            puntos.add(finPico);
        }
        Collections.sort(puntos);

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < puntos.size() - 1; i++) {
            LocalTime segIni = puntos.get(i);
            LocalTime segFin = puntos.get(i + 1);
            if (!segIni.isBefore(segFin)) {
                continue;
            }
            boolean enPico = !segIni.isBefore(inicioPico) && segIni.isBefore(finPico);
            BigDecimal precio = enPico ? precioPico : precioNormal;

            long minutos = Duration.between(segIni, segFin).toMinutes();
            BigDecimal horas = BigDecimal.valueOf(minutos)
                    .divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
            total = total.add(precio.multiply(horas));
        }
        return total;
    }
}
