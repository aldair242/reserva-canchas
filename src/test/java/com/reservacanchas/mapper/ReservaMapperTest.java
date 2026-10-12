package com.reservacanchas.mapper;

import com.reservacanchas.dto.reserva.ReservaResponse;
import com.reservacanchas.model.Cancha;
import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.enums.EstadoReserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReservaMapperTest {

    private ReservaMapper reservaMapper;

    @BeforeEach
    void setUp() {
        reservaMapper = new ReservaMapperImpl();
    }

    @Test
    void toResponse_conPartidoAsociado_mapeaPartidoId() {
        Jugador organizador = new Jugador();
        organizador.setId(1L);
        organizador.setNombre("Lionel");
        organizador.setApellido("Messi");

        Cancha cancha = new CanchaFutbol();
        cancha.setId(2L);
        cancha.setNombre("Cancha Sintetica 1");

        Partido partido = new Partido();
        partido.setId(99L);

        Reserva reserva = new Reserva();
        reserva.setId(10L);
        reserva.setCodigo("RES-12345");
        reserva.setFecha(LocalDate.of(2026, 10, 20));
        reserva.setHoraInicio(LocalTime.of(19, 0));
        reserva.setHoraFin(LocalTime.of(20, 0));
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reserva.setPrecioTotal(new BigDecimal("100.00"));
        reserva.setOrganizador(organizador);
        reserva.setCancha(cancha);
        reserva.setPartido(partido);

        ReservaResponse response = reservaMapper.toResponse(reserva);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("RES-12345", response.codigo());
        assertEquals(99L, response.partidoId());
        assertEquals(1L, response.organizadorId());
        assertEquals("Lionel Messi", response.organizadorNombre());
        assertEquals(2L, response.canchaId());
        assertFalse(response.pagada());
    }

    @Test
    void toResponse_sinPartidoAsociado_partidoIdEsNulo() {
        Jugador organizador = new Jugador();
        organizador.setId(1L);
        organizador.setNombre("Cristiano");
        organizador.setApellido("Ronaldo");

        Cancha cancha = new CanchaFutbol();
        cancha.setId(2L);
        cancha.setNombre("Cancha 2");

        Reserva reserva = new Reserva();
        reserva.setId(11L);
        reserva.setCodigo("RES-67890");
        reserva.setFecha(LocalDate.of(2026, 10, 21));
        reserva.setHoraInicio(LocalTime.of(18, 0));
        reserva.setHoraFin(LocalTime.of(19, 0));
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(new BigDecimal("80.00"));
        reserva.setOrganizador(organizador);
        reserva.setCancha(cancha);
        reserva.setPartido(null);

        ReservaResponse response = reservaMapper.toResponse(reserva);

        assertNotNull(response);
        assertEquals(11L, response.id());
        assertNull(response.partidoId());
    }
}
