package com.reservacanchas.service;

import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.dto.partido.PartidoResponse;
import com.reservacanchas.mapper.EventoMapper;
import com.reservacanchas.mapper.PartidoMapper;
import com.reservacanchas.model.EventoPartido;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.OrigenEvento;
import com.reservacanchas.model.enums.TipoEvento;
import com.reservacanchas.repository.CamaraCanchaRepository;
import com.reservacanchas.repository.EventoPartidoRepository;
import com.reservacanchas.repository.GrabacionRepository;
import com.reservacanchas.repository.InvitadoRepository;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.repository.ParticipantePartidoRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.repository.ReservaRepository;
import com.reservacanchas.service.impl.EventoServiceImpl;
import com.reservacanchas.service.impl.PartidoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartidoMisYEventosTest {

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private InvitadoRepository invitadoRepository;

    @Mock
    private CamaraCanchaRepository camaraCanchaRepository;

    @Mock
    private GrabacionRepository grabacionRepository;

    @Mock
    private PartidoMapper partidoMapper;

    @Mock
    private SecurityService securityService;

    @Mock
    private EventoPartidoRepository eventoRepository;

    @Mock
    private ParticipantePartidoRepository participanteRepository;

    @Mock
    private EventoMapper eventoMapper;

    @Mock
    private Authentication authentication;

    @Test
    void listarMisPartidos_retornaPartidosDelUsuario() {
        PartidoServiceImpl partidoService = new PartidoServiceImpl(
                partidoRepository,
                reservaRepository,
                jugadorRepository,
                invitadoRepository,
                camaraCanchaRepository,
                grabacionRepository,
                partidoMapper,
                securityService
        );

        Jugador jugador = new Jugador();
        jugador.setId(42L);
        when(securityService.getUsuarioActual(authentication)).thenReturn(jugador);

        Partido partido = new Partido();
        partido.setId(10L);
        when(partidoRepository.findMisPartidos(42L)).thenReturn(List.of(partido));

        PartidoResponse responseMock = new PartidoResponse(
                10L, null, "Equipo A", "Equipo B", 0, 0, null, null, "token-1", 1L, List.of(), List.of()
        );
        when(partidoMapper.toResponse(partido)).thenReturn(responseMock);

        List<PartidoResponse> resultado = partidoService.listarMisPartidos(authentication);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).id());
    }

    @Test
    void listarPorPartido_retornaEventosDelPartido() {
        EventoServiceImpl eventoService = new EventoServiceImpl(
                eventoRepository,
                partidoRepository,
                participanteRepository,
                eventoMapper,
                securityService
        );

        Long partidoId = 7L;
        when(partidoRepository.existsById(partidoId)).thenReturn(true);

        Gol gol = new Gol();
        gol.setId(50L);
        when(eventoRepository.findByPartidoId(partidoId)).thenReturn(List.of(gol));

        EventoResponse responseMock = new EventoResponse(
                50L, TipoEvento.GOL, LadoEquipo.A, null, 15,
                OrigenEvento.MANUAL, EstadoEvento.CONFIRMADO, "Gol de equipo", true,
                42L, "Jugador", null, null, null
        );
        when(eventoMapper.toResponse(any(EventoPartido.class))).thenReturn(responseMock);

        List<EventoResponse> resultado = eventoService.listarPorPartido(partidoId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(50L, resultado.get(0).id());
    }
}
