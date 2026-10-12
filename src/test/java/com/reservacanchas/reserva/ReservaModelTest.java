package com.reservacanchas.reserva;

import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.enums.EstadoReserva;
import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.TipoTarifa;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaModelTest {

    @Test
    void calcularMonto_debeUsarCanchaCalcularPrecio() {
        CanchaFutbol cancha = new CanchaFutbol();
        cancha.setFormato(FormatoFutbol.FUTBOL_7);
        cancha.setTipoTarifa(TipoTarifa.ESTANDAR);
        cancha.setPrecioHora(new BigDecimal("80.00"));

        Reserva reserva = new Reserva();
        reserva.setCancha(cancha);
        reserva.setFecha(LocalDate.of(2024, 6, 15));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(12, 0));

        BigDecimal monto = reserva.calcularMonto();

        assertEquals(0, new BigDecimal("160.00").compareTo(monto));
    }

    @Test
    void calcularMonto_canchaNula_debeRetornarCero() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.of(2024, 6, 15));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(12, 0));

        BigDecimal monto = reserva.calcularMonto();

        assertEquals(0, BigDecimal.ZERO.compareTo(monto));
    }

    @Test
    void puedeCancelarse_conMasDeDosHoras_debeRetornarTrue() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setFecha(LocalDate.now().plusDays(1));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(12, 0));

        assertTrue(reserva.puedeCancelarse());
    }

    @Test
    void puedeCancelarse_conMenosDeDosHoras_debeRetornarFalse() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PENDIENTE);
        // Inicio en 1 hora desde ahora
        LocalDateTime inicio = LocalDateTime.now().plusHours(1);
        reserva.setFecha(inicio.toLocalDate());
        reserva.setHoraInicio(inicio.toLocalTime());
        reserva.setHoraFin(inicio.toLocalTime().plusHours(2));

        assertFalse(reserva.puedeCancelarse());
    }

    @Test
    void puedeCancelarse_reservaCancelada_debeRetornarFalse() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.setFecha(LocalDate.now().plusDays(1));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(12, 0));

        assertFalse(reserva.puedeCancelarse());
    }

    @Test
    void cancelar_debeAlmacenarMotivoYEstado() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setFecha(LocalDate.now().plusDays(1));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(12, 0));

        reserva.cancelar("Cambio de planes");

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertEquals("Cambio de planes", reserva.getMotivoCancelacion());
    }

    @Test
    void reprogramar_debeActualizarFechasYRecalcularMonto() {
        CanchaFutbol cancha = new CanchaFutbol();
        cancha.setFormato(FormatoFutbol.FUTBOL_7);
        cancha.setTipoTarifa(TipoTarifa.ESTANDAR);
        cancha.setPrecioHora(new BigDecimal("80.00"));

        Reserva reserva = new Reserva();
        reserva.setCancha(cancha);
        reserva.setFecha(LocalDate.of(2024, 6, 15));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(12, 0));
        reserva.setPrecioTotal(new BigDecimal("160.00"));

        reserva.reprogramar(
                LocalDate.of(2024, 6, 16),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0));

        assertEquals(LocalDate.of(2024, 6, 16), reserva.getFecha());
        assertEquals(LocalTime.of(14, 0), reserva.getHoraInicio());
        assertEquals(LocalTime.of(16, 0), reserva.getHoraFin());
        assertEquals(0, new BigDecimal("160.00").compareTo(reserva.getPrecioTotal()));
    }
}
