package com.reservacanchas.tarifa;

import com.reservacanchas.model.CanchaBasquet;
import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.CanchaPadel;
import com.reservacanchas.model.CanchaVoley;
import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.model.enums.TipoSuperficie;
import com.reservacanchas.model.enums.TipoTarifa;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CanchaPrecioTest {

    private final LocalDate fecha = LocalDate.of(2024, 6, 15);

    @Test
    void calcularPrecio_tarifaEstandar_debeDelegarEnEstrategia() {
        CanchaFutbol cancha = new CanchaFutbol();
        cancha.setFormato(FormatoFutbol.FUTBOL_7);
        cancha.setTipoTarifa(TipoTarifa.ESTANDAR);
        cancha.setPrecioHora(new BigDecimal("80.00"));

        BigDecimal precio = cancha.calcularPrecio(fecha, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertEquals(0, new BigDecimal("160.00").compareTo(precio));
    }

    @Test
    void calcularPrecio_tarifaHoraPico_debeUsarPreciosNormalYPico() {
        CanchaBasquet cancha = new CanchaBasquet();
        cancha.setMedioCampo(false);
        cancha.setTipoTarifa(TipoTarifa.HORA_PICO);
        cancha.setPrecioNormal(new BigDecimal("40.00"));
        cancha.setPrecioPico(new BigDecimal("80.00"));
        cancha.setInicioPico(LocalTime.of(19, 0));
        cancha.setFinPico(LocalTime.of(23, 0));

        BigDecimal precio = cancha.calcularPrecio(fecha, LocalTime.of(18, 0), LocalTime.of(20, 0));

        assertEquals(0, new BigDecimal("120.00").compareTo(precio));
    }

    @Test
    void calcularPrecio_tarifaPromocional_debeAplicarDescuentoSobreBase() {
        CanchaPadel cancha = new CanchaPadel();
        cancha.setParedesDeVidrio(true);
        cancha.setTipoTarifa(TipoTarifa.PROMOCIONAL);
        cancha.setTipoTarifaBase(TipoTarifa.ESTANDAR);
        cancha.setPrecioHora(new BigDecimal("100.00"));
        cancha.setPorcentajeDescuento(20.0);
        cancha.setVigenteDesde(LocalDate.of(2024, 1, 1));
        cancha.setVigenteHasta(LocalDate.of(2024, 12, 31));

        BigDecimal precio = cancha.calcularPrecio(fecha, LocalTime.of(10, 0), LocalTime.of(11, 0));

        assertEquals(0, new BigDecimal("80.00").compareTo(precio));
    }

    @Test
    void getDeporte_y_capacidad_debenCoincidirConSubtipo() {
        CanchaFutbol futbol = new CanchaFutbol();
        futbol.setFormato(FormatoFutbol.FUTBOL_5);
        assertEquals(TipoDeporte.FUTBOL, futbol.getDeporte());
        assertEquals(10, futbol.getCapacidadJugadores());

        CanchaBasquet basquet = new CanchaBasquet();
        basquet.setMedioCampo(true);
        assertEquals(TipoDeporte.BASQUET, basquet.getDeporte());
        assertEquals(6, basquet.getCapacidadJugadores());

        CanchaVoley voley = new CanchaVoley();
        voley.setEsDePlaya(false);
        assertEquals(TipoDeporte.VOLEY, voley.getDeporte());
        assertEquals(12, voley.getCapacidadJugadores());

        CanchaPadel padel = new CanchaPadel();
        assertEquals(TipoDeporte.PADEL, padel.getDeporte());
        assertEquals(4, padel.getCapacidadJugadores());
    }
}
