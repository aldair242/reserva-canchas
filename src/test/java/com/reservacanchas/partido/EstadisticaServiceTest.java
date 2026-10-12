package com.reservacanchas.partido;

import com.reservacanchas.dto.estadistica.EstadisticaResponse;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.repository.EventoPartidoRepository;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.repository.ParticipantePartidoRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.service.impl.EstadisticaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadisticaServiceTest {

    @Mock
    private ParticipantePartidoRepository participanteRepository;

    @Mock
    private EventoPartidoRepository eventoRepository;

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @InjectMocks
    private EstadisticaServiceImpl estadisticaService;

    private Jugador jugador;

    @BeforeEach
    void setup() {
        jugador = new Jugador();
        jugador.setId(1L);
        jugador.setNombre("Juan");
        jugador.setApellido("Perez");
    }

    @Test
    void calcularEstadisticas_conPartidosYGoles() {
        Partido partido = new Partido();
        partido.setEstado(EstadoPartido.FINALIZADO);
        partido.setGolesEquipoA(3);
        partido.setGolesEquipoB(1);

        ParticipantePartido pp = new ParticipantePartido();
        pp.setPersona(jugador);
        pp.setEquipo(LadoEquipo.A);
        pp.setPartido(partido);

        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugador));
        when(participanteRepository.findPartidosJugados(eq(1L), eq(EstadoPartido.FINALIZADO),
                any(), any(), any())).thenReturn(List.of(pp));
        when(eventoRepository.contarGoles(eq(EstadoEvento.CONFIRMADO), eq(1L),
                any(), any(), any())).thenReturn(5L);
        when(eventoRepository.contarAsistencias(eq(EstadoEvento.CONFIRMADO), eq(1L),
                any(), any(), any())).thenReturn(2L);

        EstadisticaResponse stats = estadisticaService.calcularEstadisticas(1L, null, null, null);

        assertEquals(1, stats.partidosJugados());
        assertEquals(5, stats.goles());
        assertEquals(2, stats.asistencias());
        assertEquals(7, stats.participacionesEnGol());
        assertEquals(1, stats.victorias());
        assertEquals(0, stats.empates());
        assertEquals(0, stats.derrotas());
        assertEquals(100.0, stats.porcentajeVictorias(), 0.01);
        assertEquals(5.0, stats.golesPorPartido(), 0.01);
    }

    @Test
    void calcularEstadisticas_sinPartidos() {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugador));
        when(participanteRepository.findPartidosJugados(eq(1L), eq(EstadoPartido.FINALIZADO),
                any(), any(), any())).thenReturn(List.of());
        when(eventoRepository.contarGoles(eq(EstadoEvento.CONFIRMADO), eq(1L),
                any(), any(), any())).thenReturn(0L);
        when(eventoRepository.contarAsistencias(eq(EstadoEvento.CONFIRMADO), eq(1L),
                any(), any(), any())).thenReturn(0L);

        EstadisticaResponse stats = estadisticaService.calcularEstadisticas(1L, null, null, null);

        assertEquals(0, stats.partidosJugados());
        assertEquals(0, stats.goles());
        assertEquals(0, stats.golesPorPartido(), 0.01);
        assertEquals(0.0, stats.porcentajeVictorias(), 0.01);
    }
}
