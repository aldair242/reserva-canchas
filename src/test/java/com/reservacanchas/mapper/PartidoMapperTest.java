package com.reservacanchas.mapper;

import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.dto.partido.PartidoResponse;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.GolEnContra;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.TarjetaAmarilla;
import com.reservacanchas.model.TarjetaRoja;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.OrigenEvento;
import com.reservacanchas.model.enums.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartidoMapperTest {

    private PartidoMapper partidoMapper;

    @BeforeEach
    void setUp() {
        partidoMapper = new PartidoMapperImpl();
    }

    @Test
    void toResponse_partidoConEventosPolimorficos_mapeaTodosLosCamposCorrectamente() {
        Partido partido = new Partido();
        partido.setId(10L);
        partido.setEstado(EstadoPartido.FINALIZADO);
        partido.setNombreEquipoA("Los Halcones");
        partido.setNombreEquipoB("Los Tigres");
        partido.setGolesEquipoA(2);
        partido.setGolesEquipoB(1);
        partido.setInicioReal(LocalDateTime.of(2026, 10, 11, 18, 0));
        partido.setFinReal(LocalDateTime.of(2026, 10, 11, 19, 0));

        Jugador j1 = new Jugador();
        j1.setId(101L);
        j1.setNombre("Juan");
        j1.setApellido("Perez");

        Jugador j2 = new Jugador();
        j2.setId(102L);
        j2.setNombre("Pedro");
        j2.setApellido("Gomez");

        Jugador j3 = new Jugador();
        j3.setId(103L);
        j3.setNombre("Carlos");
        j3.setApellido("Lopez");

        Jugador j4 = new Jugador();
        j4.setId(104L);
        j4.setNombre("Luis");
        j4.setApellido("Diaz");

        ParticipantePartido p1 = new ParticipantePartido();
        p1.setId(1L);
        p1.setPersona(j1);
        p1.setEquipo(LadoEquipo.A);

        ParticipantePartido p2 = new ParticipantePartido();
        p2.setId(2L);
        p2.setPersona(j2);
        p2.setEquipo(LadoEquipo.A);

        ParticipantePartido p3 = new ParticipantePartido();
        p3.setId(3L);
        p3.setPersona(j3);
        p3.setEquipo(LadoEquipo.B);

        ParticipantePartido p4 = new ParticipantePartido();
        p4.setId(4L);
        p4.setPersona(j4);
        p4.setEquipo(LadoEquipo.B);

        // 1. Gol con anotador y asistente
        Gol gol = new Gol();
        gol.setId(501L);
        gol.setEquipo(LadoEquipo.A);
        gol.setInstante(LocalDateTime.of(2026, 10, 11, 18, 15));
        gol.setMinuto(15);
        gol.setOrigen(OrigenEvento.MANUAL);
        gol.setEstado(EstadoEvento.CONFIRMADO);
        gol.atribuir(p1, p2);

        // 2. Gol en contra
        GolEnContra golEnContra = new GolEnContra();
        golEnContra.setId(502L);
        golEnContra.setEquipo(LadoEquipo.A);
        golEnContra.setInstante(LocalDateTime.of(2026, 10, 11, 18, 30));
        golEnContra.setMinuto(30);
        golEnContra.setOrigen(OrigenEvento.MANUAL);
        golEnContra.setEstado(EstadoEvento.CONFIRMADO);
        golEnContra.setAutor(p3);

        // 3. Tarjeta Amarilla
        TarjetaAmarilla amarilla = new TarjetaAmarilla();
        amarilla.setId(503L);
        amarilla.setEquipo(LadoEquipo.B);
        amarilla.setInstante(LocalDateTime.of(2026, 10, 11, 18, 40));
        amarilla.setMinuto(40);
        amarilla.setOrigen(OrigenEvento.MANUAL);
        amarilla.setEstado(EstadoEvento.CONFIRMADO);
        amarilla.setJugador(p3);
        amarilla.setMotivo("Falta tactica");

        // 4. Tarjeta Roja
        TarjetaRoja roja = new TarjetaRoja();
        roja.setId(504L);
        roja.setEquipo(LadoEquipo.B);
        roja.setInstante(LocalDateTime.of(2026, 10, 11, 18, 50));
        roja.setMinuto(50);
        roja.setOrigen(OrigenEvento.MANUAL);
        roja.setEstado(EstadoEvento.CONFIRMADO);
        roja.setJugador(p4);
        roja.setMotivo("Conducta violenta");

        partido.setParticipantes(List.of(p1, p2, p3, p4));
        partido.setEventos(List.of(gol, golEnContra, amarilla, roja));

        PartidoResponse response = partidoMapper.toResponse(partido);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(EstadoPartido.FINALIZADO, response.estado());
        assertEquals("Los Halcones", response.nombreEquipoA());
        assertEquals("Los Tigres", response.nombreEquipoB());
        assertEquals(4, response.eventos().size());

        // Verificacion Gol
        EventoResponse evGol = response.eventos().get(0);
        assertEquals(TipoEvento.GOL, evGol.tipo());
        assertEquals("Gol de Juan Perez (asistencia de Pedro Gomez)", evGol.descripcion());
        assertTrue(evGol.afectaMarcador());
        assertEquals(1L, evGol.anotadorId());
        assertEquals("Juan Perez", evGol.anotadorNombre());
        assertEquals(2L, evGol.asistenteId());
        assertEquals("Pedro Gomez", evGol.asistenteNombre());
        assertNull(evGol.motivo());

        // Verificacion Gol en contra
        EventoResponse evGec = response.eventos().get(1);
        assertEquals(TipoEvento.GOL_EN_CONTRA, evGec.tipo());
        assertEquals("Gol en contra de Carlos Lopez", evGec.descripcion());
        assertTrue(evGec.afectaMarcador());
        assertNull(evGec.anotadorId());
        assertNull(evGec.asistenteId());
        assertNull(evGec.motivo());

        // Verificacion Tarjeta Amarilla
        EventoResponse evAma = response.eventos().get(2);
        assertEquals(TipoEvento.TARJETA_AMARILLA, evAma.tipo());
        assertEquals("Tarjeta amarilla para Carlos Lopez: Falta tactica", evAma.descripcion());
        assertFalse(evAma.afectaMarcador());
        assertEquals("Falta tactica", evAma.motivo());
        assertNull(evAma.anotadorId());
        assertNull(evAma.asistenteId());

        // Verificacion Tarjeta Roja
        EventoResponse evRoja = response.eventos().get(3);
        assertEquals(TipoEvento.TARJETA_ROJA, evRoja.tipo());
        assertEquals("Tarjeta roja para Luis Diaz: Conducta violenta", evRoja.descripcion());
        assertFalse(evRoja.afectaMarcador());
        assertEquals("Conducta violenta", evRoja.motivo());
        assertNull(evRoja.anotadorId());
        assertNull(evRoja.asistenteId());
    }
}
