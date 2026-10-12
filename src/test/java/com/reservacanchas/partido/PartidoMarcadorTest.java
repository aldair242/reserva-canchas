package com.reservacanchas.partido;

import com.reservacanchas.model.EventoPartido;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.GolEnContra;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.TarjetaAmarilla;
import com.reservacanchas.model.TarjetaRoja;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.OrigenEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartidoMarcadorTest {

    private Partido partido;
    private ParticipantePartido jugadorA;
    private ParticipantePartido jugadorB;

    @BeforeEach
    void setup() {
        partido = new Partido();
        partido.setGolesEquipoA(0);
        partido.setGolesEquipoB(0);

        Jugador jA = new Jugador();
        jA.setNombre("Juan");
        jA.setApellido("Perez");
        jugadorA = new ParticipantePartido();
        jugadorA.setPersona(jA);
        jugadorA.setEquipo(LadoEquipo.A);

        Jugador jB = new Jugador();
        jB.setNombre("Pedro");
        jB.setApellido("Gomez");
        jugadorB = new ParticipantePartido();
        jugadorB.setPersona(jB);
        jugadorB.setEquipo(LadoEquipo.B);
    }

    @Test
    void recalcularMarcador_soloGolesConfirmados() {
        Gol golA = crearGol(LadoEquipo.A, jugadorA, null, EstadoEvento.CONFIRMADO);
        Gol golB = crearGol(LadoEquipo.B, jugadorB, null, EstadoEvento.CONFIRMADO);
        Gol golPendiente = crearGol(LadoEquipo.A, jugadorA, null, EstadoEvento.PENDIENTE_ATRIBUCION);

        partido.registrarEvento(golA);
        partido.registrarEvento(golB);
        partido.registrarEvento(golPendiente);

        partido.recalcularMarcador();

        assertEquals(1, partido.getGolesEquipoA());
        assertEquals(1, partido.getGolesEquipoB());
    }

    @Test
    void recalcularMarcador_tarjetasNoAfectanMarcador() {
        Gol golA = crearGol(LadoEquipo.A, jugadorA, null, EstadoEvento.CONFIRMADO);
        TarjetaAmarilla amarilla = crearTarjetaAmarilla(LadoEquipo.B, jugadorB);
        TarjetaRoja roja = crearTarjetaRoja(LadoEquipo.A, jugadorA);

        partido.registrarEvento(golA);
        partido.registrarEvento(amarilla);
        partido.registrarEvento(roja);

        partido.recalcularMarcador();

        assertEquals(1, partido.getGolesEquipoA());
        assertEquals(0, partido.getGolesEquipoB());
    }

    @Test
    void recalcularMarcador_golEnContraSumaAlEquipoContrario() {
        // Gol en contra del jugador B (autor) -> suma al equipo A
        GolEnContra golEnContra = new GolEnContra();
        golEnContra.setEquipo(LadoEquipo.A); // el gol va para el equipo A
        golEnContra.setEstado(EstadoEvento.CONFIRMADO);
        golEnContra.setInstante(LocalDateTime.now());
        golEnContra.setOrigen(OrigenEvento.MANUAL);
        golEnContra.setAutor(jugadorB);

        partido.registrarEvento(golEnContra);
        partido.recalcularMarcador();

        assertEquals(1, partido.getGolesEquipoA());
        assertEquals(0, partido.getGolesEquipoB());
    }

    @Test
    void recalcularMarcador_eventoDescartadoNoCuenta() {
        Gol golA = crearGol(LadoEquipo.A, jugadorA, null, EstadoEvento.CONFIRMADO);
        Gol golDescartado = crearGol(LadoEquipo.A, jugadorA, null, EstadoEvento.DESCARTADO);

        partido.registrarEvento(golA);
        partido.registrarEvento(golDescartado);

        partido.recalcularMarcador();

        assertEquals(1, partido.getGolesEquipoA());
    }

    @Test
    void obtenerGanador_empateRetornaNull() {
        Gol golA = crearGol(LadoEquipo.A, jugadorA, null, EstadoEvento.CONFIRMADO);
        Gol golB = crearGol(LadoEquipo.B, jugadorB, null, EstadoEvento.CONFIRMADO);
        partido.registrarEvento(golA);
        partido.registrarEvento(golB);
        partido.recalcularMarcador();

        assertEquals(null, partido.obtenerGanador());
    }

    @Test
    void obtenerGanador_equipoAGana() {
        Gol golA1 = crearGol(LadoEquipo.A, jugadorA, null, EstadoEvento.CONFIRMADO);
        Gol golA2 = crearGol(LadoEquipo.A, jugadorA, null, EstadoEvento.CONFIRMADO);
        Gol golB = crearGol(LadoEquipo.B, jugadorB, null, EstadoEvento.CONFIRMADO);
        partido.registrarEvento(golA1);
        partido.registrarEvento(golA2);
        partido.registrarEvento(golB);
        partido.recalcularMarcador();

        assertEquals(LadoEquipo.A, partido.obtenerGanador());
    }

    @Test
    void tarjetaRoja_generaExpulsion() {
        TarjetaRoja roja = crearTarjetaRoja(LadoEquipo.A, jugadorA);
        assertTrue(roja.generaExpulsion());
    }

    @Test
    void tarjetaAmarilla_descripcion() {
        TarjetaAmarilla amarilla = crearTarjetaAmarilla(LadoEquipo.A, jugadorA);
        assertTrue(amarilla.descripcion().contains("Tarjeta amarilla"));
    }

    @Test
    void afectaMarcador_polimorfismo() {
        Gol gol = crearGol(LadoEquipo.A, jugadorA, null, EstadoEvento.CONFIRMADO);
        GolEnContra golEnContra = new GolEnContra();
        TarjetaAmarilla amarilla = crearTarjetaAmarilla(LadoEquipo.A, jugadorA);
        TarjetaRoja roja = crearTarjetaRoja(LadoEquipo.A, jugadorA);

        assertTrue(gol.afectaMarcador());
        assertTrue(golEnContra.afectaMarcador());
        assertFalse(amarilla.afectaMarcador());
        assertFalse(roja.afectaMarcador());
    }

    private Gol crearGol(LadoEquipo equipo, ParticipantePartido anotador,
                         ParticipantePartido asistente, EstadoEvento estado) {
        Gol gol = new Gol();
        gol.setEquipo(equipo);
        gol.setEstado(estado);
        gol.setInstante(LocalDateTime.now());
        gol.setOrigen(OrigenEvento.MANUAL);
        gol.setAnotador(anotador);
        gol.setAsistente(asistente);
        return gol;
    }

    private TarjetaAmarilla crearTarjetaAmarilla(LadoEquipo equipo, ParticipantePartido jugador) {
        TarjetaAmarilla t = new TarjetaAmarilla();
        t.setEquipo(equipo);
        t.setEstado(EstadoEvento.CONFIRMADO);
        t.setInstante(LocalDateTime.now());
        t.setOrigen(OrigenEvento.MANUAL);
        t.setJugador(jugador);
        t.setMotivo("Falta");
        return t;
    }

    private TarjetaRoja crearTarjetaRoja(LadoEquipo equipo, ParticipantePartido jugador) {
        TarjetaRoja t = new TarjetaRoja();
        t.setEquipo(equipo);
        t.setEstado(EstadoEvento.CONFIRMADO);
        t.setInstante(LocalDateTime.now());
        t.setOrigen(OrigenEvento.MANUAL);
        t.setJugador(jugador);
        t.setMotivo("Expulsion");
        return t;
    }
}
