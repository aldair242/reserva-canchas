package com.reservacanchas.tarifa;

import com.reservacanchas.model.tarifa.EstrategiaTarifa;
import com.reservacanchas.model.tarifa.TarifaEstandar;
import com.reservacanchas.model.tarifa.TarifaPromocional;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarifaPromocionalTest {

    private final EstrategiaTarifa base = new TarifaEstandar(new BigDecimal("100.00"));
    private final LocalTime inicio = LocalTime.of(10, 0);
    private final LocalTime fin = LocalTime.of(11, 0);

    @Test
    void calcular_dentroDeVigencia_conDescuento_debeAplicarDescuento() {
        TarifaPromocional promocional = new TarifaPromocional(
                base, 10.0,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

        BigDecimal resultado = promocional.calcular(null, LocalDate.of(2024, 6, 15), inicio, fin);
        assertEquals(0, new BigDecimal("90.00").compareTo(resultado));
    }

    @Test
    void calcular_fueraDeVigencia_debeRetornarPrecioBase() {
        TarifaPromocional promocional = new TarifaPromocional(
                base, 10.0,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

        BigDecimal resultado = promocional.calcular(null, LocalDate.of(2025, 6, 15), inicio, fin);
        assertEquals(0, new BigDecimal("100.00").compareTo(resultado));
    }

    @Test
    void calcular_sinDescuento_debeRetornarPrecioBase() {
        TarifaPromocional promocional = new TarifaPromocional(
                base, 0.0,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

        BigDecimal resultado = promocional.calcular(null, LocalDate.of(2024, 6, 15), inicio, fin);
        assertEquals(0, new BigDecimal("100.00").compareTo(resultado));
    }

    @Test
    void calcular_descuentoCincuentaPorCiento_debeRetornarMitad() {
        TarifaPromocional promocional = new TarifaPromocional(
                base, 50.0,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

        BigDecimal resultado = promocional.calcular(null, LocalDate.of(2024, 6, 15), inicio, fin);
        assertEquals(0, new BigDecimal("50.00").compareTo(resultado));
    }
}
