package com.reservacanchas.service;

import com.reservacanchas.dto.partido.ParticipanteRequest;
import com.reservacanchas.dto.partido.PartidoResponse;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.mapper.PartidoMapper;
import com.reservacanchas.model.*;
import com.reservacanchas.model.enums.EstadoGrabacion;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.EstadoReserva;
import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.repository.CamaraCanchaRepository;
import com.reservacanchas.repository.GrabacionRepository;
import com.reservacanchas.repository.InvitadoRepository;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.repository.ReservaRepository;
import com.reservacanchas.service.impl.PartidoCicloVidaService;
import com.reservacanchas.service.impl.PartidoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartidoCicloVidaYParticipantesTest {

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
    private Authentication authentication;

    @Test
    void agregarParticipante_jugadorDuplicado_lanzaOperacionNoPermitidaException() {
        PartidoServiceImpl service = new PartidoServiceImpl(
                partidoRepository, reservaRepository, jugadorRepository,
                invitadoRepository, camaraCanchaRepository, grabacionRepository,
                partidoMapper, securityService
        );

        Jugador amigo = new Jugador();
        amigo.setId(99L);
        amigo.setNombre("Amigo");
        amigo.setApellido("Perez");

        when(securityService.getUsuarioActual(authentication)).thenReturn(amigo);

        Jugador organizador = new Jugador();
        organizador.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(10L);
        reserva.setOrganizador(organizador);

        Partido partido = new Partido();
        partido.setId(20L);
        partido.setReserva(reserva);

        // Ya tiene a 'amigo' como participante
        ParticipantePartido part1 = new ParticipantePartido();
        part1.setPersona(amigo);
        part1.setEquipo(LadoEquipo.A);
        partido.agregarParticipante(part1);

        when(partidoRepository.findById(20L)).thenReturn(Optional.of(partido));
        when(jugadorRepository.findById(99L)).thenReturn(Optional.of(amigo));

        ParticipanteRequest request = new ParticipanteRequest(99L, null, null, LadoEquipo.B);

        OperacionNoPermitidaException ex = assertThrows(OperacionNoPermitidaException.class, () ->
                service.agregarParticipante(20L, request, authentication)
        );

        assertTrue(ex.getMessage().contains("ya se encuentra registrado"));
    }

    @Test
    void agregarParticipante_amigoSeUneASiMismo_agregaCorrectamente() {
        PartidoServiceImpl service = new PartidoServiceImpl(
                partidoRepository, reservaRepository, jugadorRepository,
                invitadoRepository, camaraCanchaRepository, grabacionRepository,
                partidoMapper, securityService
        );

        Jugador amigo = new Jugador();
        amigo.setId(99L);
        amigo.setNombre("Amigo");
        amigo.setApellido("Perez");

        when(securityService.getUsuarioActual(authentication)).thenReturn(amigo);

        Jugador organizador = new Jugador();
        organizador.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(10L);
        reserva.setOrganizador(organizador);

        Partido partido = new Partido();
        partido.setId(20L);
        partido.setReserva(reserva);

        when(partidoRepository.findById(20L)).thenReturn(Optional.of(partido));
        when(jugadorRepository.findById(99L)).thenReturn(Optional.of(amigo));
        when(partidoRepository.save(any(Partido.class))).thenReturn(partido);

        ParticipanteRequest request = new ParticipanteRequest(99L, null, null, LadoEquipo.B);

        service.agregarParticipante(20L, request, authentication);

        assertEquals(1, partido.getParticipantes().size());
        assertEquals(LadoEquipo.B, partido.getParticipantes().get(0).getEquipo());
    }

    @Test
    void cicloVida_partidoEnHorarioActual_iniciaPartidoYGrabacion() {
        PartidoCicloVidaService cicloService = new PartidoCicloVidaService(
                reservaRepository, partidoRepository, camaraCanchaRepository, grabacionRepository
        );

        LocalDate hoy = LocalDate.now();
        LocalTime inicio = LocalTime.now().minusMinutes(10);
        LocalTime fin = LocalTime.now().plusMinutes(50);

        CanchaFutbol cancha = new CanchaFutbol();
        cancha.setId(5L);
        cancha.setFormato(FormatoFutbol.FUTBOL_5);

        Jugador organizador = new Jugador();
        organizador.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(100L);
        reserva.setCancha(cancha);
        reserva.setOrganizador(organizador);
        reserva.setFecha(hoy);
        reserva.setHoraInicio(inicio);
        reserva.setHoraFin(fin);
        reserva.confirmar();

        Partido partido = new Partido();
        partido.setId(200L);
        partido.setReserva(reserva);
        partido.setEstado(EstadoPartido.PROGRAMADO);

        CamaraCancha camara = new CamaraCancha();
        camara.setId(8L);
        camara.setActivo(true);

        when(reservaRepository.findByEstadoIn(any())).thenReturn(List.of(reserva));
        when(partidoRepository.findByReservaId(100L)).thenReturn(Optional.of(partido));
        when(partidoRepository.save(any(Partido.class))).thenReturn(partido);
        when(camaraCanchaRepository.findFirstByCanchaIdAndActivoTrue(5L)).thenReturn(Optional.of(camara));
        when(grabacionRepository.findByPartidoIdAndEstado(200L, EstadoGrabacion.GRABANDO)).thenReturn(List.of());

        cicloService.procesarCicloDeVida();

        assertEquals(EstadoPartido.EN_JUEGO, partido.getEstado());
        verify(grabacionRepository, times(1)).save(any(Grabacion.class));
    }

    @Test
    void cicloVida_partidoFinalizadoPorHorario_completaReservaYFinalizaGrabacion() {
        PartidoCicloVidaService cicloService = new PartidoCicloVidaService(
                reservaRepository, partidoRepository, camaraCanchaRepository, grabacionRepository
        );

        LocalDate ayer = LocalDate.now().minusDays(1);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        Reserva reserva = new Reserva();
        reserva.setId(101L);
        reserva.setFecha(ayer);
        reserva.setHoraInicio(inicio);
        reserva.setHoraFin(fin);
        reserva.confirmar();

        Partido partido = new Partido();
        partido.setId(201L);
        partido.setReserva(reserva);
        partido.iniciar();

        Grabacion grabacion = new Grabacion();
        grabacion.setId(301L);
        grabacion.setEstado(EstadoGrabacion.GRABANDO);

        when(reservaRepository.findByEstadoIn(any())).thenReturn(List.of(reserva));
        when(partidoRepository.findByReservaId(101L)).thenReturn(Optional.of(partido));
        when(grabacionRepository.findByPartidoIdAndEstado(201L, EstadoGrabacion.GRABANDO))
                .thenReturn(List.of(grabacion));

        cicloService.procesarCicloDeVida();

        assertEquals(EstadoPartido.FINALIZADO, partido.getEstado());
        assertEquals(EstadoGrabacion.FINALIZADA, grabacion.getEstado());
        assertEquals(EstadoReserva.COMPLETADA, reserva.getEstado());
    }

    @Test
    void reservaPasada_puedeReprogramarseYPuedeCancelarse_retornanFalse() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now().minusDays(1));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(11, 0));
        reserva.confirmar();

        assertFalse(reserva.puedeReprogramarse());
        assertFalse(reserva.puedeCancelarse());
    }
}
