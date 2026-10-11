package com.reservacanchas.tarifa;

import com.reservacanchas.model.tarifa.TarifaEstandar;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarifaEstandarTest {

    private final TarifaEstandar tarifa = new TarifaEstandar(new BigDecimal("100.00"));
    private final LocalDate fecha = LocalDate.of(2024, 6, 15);

    @Test
    void calcular_unaHora_debeRetornarPrecioHora() {
        BigDecimal resultado = tarifa.calcular(null, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertEquals(0, new BigDecimal("100.00").compareTo(resultado));
    }

    @Test
    void calcular_dosHoras_debeRetornarDoblePrecioHora() {
        BigDecimal resultado = tarifa.calcular(null, fecha, LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertEquals(0, new BigDecimal("200.00").compareTo(resultado));
    }

    @Test
    void calcular_mediaHora_debeRetornarMitadPrecioHora() {
        BigDecimal resultado = tarifa.calcular(null, fecha, LocalTime.of(10, 0), LocalTime.of(10, 30));
        assertEquals(0, new BigDecimal("50.00").compareTo(resultado));
    }

    @Test
    void calcular_horaYMedia_debeRetornarUnoPuntoCincoVecesPrecioHora() {
        BigDecimal resultado = tarifa.calcular(null, fecha, LocalTime.of(10, 0), LocalTime.of(11, 30));
        assertEquals(0, new BigDecimal("150.00").compareTo(resultado));
    }
}
