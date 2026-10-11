package com.reservacanchas.model.tarifa;

import com.reservacanchas.model.Cancha;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public interface EstrategiaTarifa {

    BigDecimal calcular(Cancha cancha, LocalDate fecha, LocalTime inicio, LocalTime fin);
}
