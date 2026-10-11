package com.reservacanchas.tarifa;

import com.reservacanchas.model.tarifa.TarifaHoraPico;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarifaHoraPicoTest {

    private final TarifaHoraPico tarifa = new TarifaHoraPico(
            new BigDecimal("50.00"), new BigDecimal("100.00"),
            LocalTime.of(18, 0), LocalTime.of(22, 0));
    private final LocalDate fecha = LocalDate.of(2024, 6, 15);

    @Test
    void calcular_fueraDePico_debeUsarPrecioNormal() {
        BigDecimal resultado = tarifa.calcular(null, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertEquals(0, new BigDecimal("50.00").compareTo(resultado));
    }

    @Test
    void calcular_dentroDePico_debeUsarPrecioPico() {
        BigDecimal resultado = tarifa.calcular(null, fecha, LocalTime.of(18, 0), LocalTime.of(19, 0));
        assertEquals(0, new BigDecimal("100.00").compareTo(resultado));
    }

    @Test
    void calcular_tramoMixto_debeSumarNormalYPico() {
        BigDecimal resultado = tarifa.calcular(null, fecha, LocalTime.of(17, 0), LocalTime.of(19, 0));
        assertEquals(0, new BigDecimal("150.00").compareTo(resultado));
    }

    @Test
    void calcular_tramoQueCruzaFinDePico_debeSumarPicoYNormal() {
        BigDecimal resultado = tarifa.calcular(null, fecha, LocalTime.of(21, 30), LocalTime.of(22, 30));
        assertEquals(0, new BigDecimal("75.00").compareTo(resultado));
    }
}
