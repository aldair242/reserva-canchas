package com.reservacanchas.service.impl;

import com.reservacanchas.dto.partido.AtribuirRequest;
import com.reservacanchas.dto.partido.EventoRequest;
import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.mapper.EventoMapper;
import com.reservacanchas.model.EventoPartido;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.GolEnContra;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.TarjetaAmarilla;
import com.reservacanchas.model.TarjetaRoja;
import com.reservacanchas.model.Usuario;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.OrigenEvento;
import com.reservacanchas.repository.EventoPartidoRepository;
import com.reservacanchas.repository.ParticipantePartidoRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.service.EventoService;
import com.reservacanchas.service.SecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoServiceImpl implements EventoService {

    private final EventoPartidoRepository eventoRepository;
    private final PartidoRepository partidoRepository;
    private final ParticipantePartidoRepository participanteRepository;
    private final EventoMapper eventoMapper;
    private final SecurityService securityService;

    @Value("${partidos.atribucion.horas-limite:48}")
    private int horasLimiteAtribucion;

    public EventoServiceImpl(EventoPartidoRepository eventoRepository,
                             PartidoRepository partidoRepository,
                             ParticipantePartidoRepository participanteRepository,
                             EventoMapper eventoMapper,
                             SecurityService securityService) {
        this.eventoRepository = eventoRepository;
        this.partidoRepository = partidoRepository;
        this.participanteRepository = participanteRepository;
        this.eventoMapper = eventoMapper;
        this.securityService = securityService;
    }

    @Override
    @Transactional
    public EventoResponse registrarEvento(Long partidoId, EventoRequest request,
                                          Authentication authentication) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Partido", partidoId));

        if (partido.getEstado() != EstadoPartido.EN_JUEGO
                && partido.getEstado() != EstadoPartido.PROGRAMADO) {
            throw new OperacionNoPermitidaException(
                    "No se pueden registrar eventos en un partido en estado " + partido.getEstado());
        }

        LocalDateTime instante = request.instante() != null
                ? request.instante() : LocalDateTime.now();
        int minuto = request.minuto() != null ? request.minuto() : 0;

        EventoPartido evento = switch (request.tipo()) {
            case GOL -> crearGol(partido, request, instante, minuto);
            case GOL_EN_CONTRA -> crearGolEnContra(partido, request, instante, minuto);
            case TARJETA_AMARILLA -> crearTarjetaAmarilla(partido, request, instante, minuto);
            case TARJETA_ROJA -> crearTarjetaRoja(partido, request, instante, minuto);
        };

        partido.registrarEvento(evento);
        EventoPartido guardado = eventoRepository.save(evento);
        partido.recalcularMarcador();
        partidoRepository.save(partido);
        return eventoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoResponse> listarPorPartido(Long partidoId) {
        if (!partidoRepository.existsById(partidoId)) {
            throw new ResourceNotFoundException("Partido", partidoId);
        }
        return eventoRepository.findByPartidoId(partidoId).stream()
                .map(eventoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public EventoResponse atribuir(Long eventoId, AtribuirRequest request,
                                   Authentication authentication) {
        Jugador jugador = obtenerJugadorAutenticado(authentication);
        EventoPartido evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventoId));

        if (!(evento instanceof Gol gol)) {
            throw new OperacionNoPermitidaException("Solo los goles pueden ser atribuidos");
        }
        if (gol.getEstado() != EstadoEvento.PENDIENTE_ATRIBUCION) {
            throw new OperacionNoPermitidaException(
                    "El evento no esta en estado de pendiente de atribucion");
        }
        if (gol.estaVencido()) {
            throw new OperacionNoPermitidaException(
                    "El plazo de atribucion ha vencido");
        }

        // El atribuidor debe ser participante del partido
        validarParticipanteJugador(gol.getPartido(), jugador);

        ParticipantePartido anotador = null;
        ParticipantePartido asistente = null;
        if (request.anotadorId() != null) {
            anotador = obtenerParticipanteDelPartido(gol.getPartido(), request.anotadorId());
        }
        if (request.asistenteId() != null) {
            asistente = obtenerParticipanteDelPartido(gol.getPartido(), request.asistenteId());
        }

        gol.atribuir(anotador, asistente);
        gol.setAtribuidor(jugador);
        EventoPartido guardado = eventoRepository.save(gol);
        return eventoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public EventoResponse confirmar(Long eventoId, Authentication authentication) {
        Jugador jugador = obtenerJugadorAutenticado(authentication);
        EventoPartido evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventoId));

        if (evento.getEstado() != EstadoEvento.PENDIENTE_ATRIBUCION) {
            throw new OperacionNoPermitidaException(
                    "El evento no esta en estado de pendiente de atribucion");
        }
        if (evento.getAtribuidor() == null) {
            throw new OperacionNoPermitidaException(
                    "El evento debe ser atribuido antes de confirmarse");
        }
        if (evento.getAtribuidor().getId().equals(jugador.getId())) {
            throw new OperacionNoPermitidaException(
                    "El confirmador debe ser distinto del atribuidor");
        }

        validarParticipanteJugador(evento.getPartido(), jugador);
        evento.confirmar(jugador);
        EventoPartido guardado = eventoRepository.save(evento);

        evento.getPartido().recalcularMarcador();
        partidoRepository.save(evento.getPartido());
        return eventoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public EventoResponse descartar(Long eventoId, Authentication authentication) {
        Jugador jugador = obtenerJugadorAutenticado(authentication);
        EventoPartido evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventoId));

        validarParticipanteJugador(evento.getPartido(), jugador);
        evento.descartar();
        EventoPartido guardado = eventoRepository.save(evento);

        evento.getPartido().recalcularMarcador();
        partidoRepository.save(evento.getPartido());
        return eventoMapper.toResponse(guardado);
    }

    private Gol crearGol(Partido partido, EventoRequest request,
                         LocalDateTime instante, int minuto) {
        Gol gol = new Gol();
        gol.setPartido(partido);
        gol.setEquipo(request.equipo());
        gol.setInstante(instante);
        gol.setMinuto(minuto);
        gol.setOrigen(OrigenEvento.MANUAL);
        gol.setEstado(EstadoEvento.PENDIENTE_ATRIBUCION);
        gol.setFechaLimiteAtribucion(LocalDateTime.now().plusHours(horasLimiteAtribucion));
        return gol;
    }

    private GolEnContra crearGolEnContra(Partido partido, EventoRequest request,
                                         LocalDateTime instante, int minuto) {
        GolEnContra gol = new GolEnContra();
        gol.setPartido(partido);
        gol.setEquipo(request.equipo());
        gol.setInstante(instante);
        gol.setMinuto(minuto);
        gol.setOrigen(OrigenEvento.MANUAL);
        gol.setEstado(EstadoEvento.CONFIRMADO);
        gol.setAutor(obtenerParticipanteDelPartido(partido, request.jugadorParticipanteId()));
        return gol;
    }

    private TarjetaAmarilla crearTarjetaAmarilla(Partido partido, EventoRequest request,
                                                 LocalDateTime instante, int minuto) {
        TarjetaAmarilla tarjeta = new TarjetaAmarilla();
        tarjeta.setPartido(partido);
        tarjeta.setEquipo(request.equipo());
        tarjeta.setInstante(instante);
        tarjeta.setMinuto(minuto);
        tarjeta.setOrigen(OrigenEvento.MANUAL);
        tarjeta.setEstado(EstadoEvento.CONFIRMADO);
        tarjeta.setJugador(obtenerParticipanteDelPartido(partido, request.jugadorParticipanteId()));
        tarjeta.setMotivo(request.motivo());
        return tarjeta;
    }

    private TarjetaRoja crearTarjetaRoja(Partido partido, EventoRequest request,
                                         LocalDateTime instante, int minuto) {
        TarjetaRoja tarjeta = new TarjetaRoja();
        tarjeta.setPartido(partido);
        tarjeta.setEquipo(request.equipo());
        tarjeta.setInstante(instante);
        tarjeta.setMinuto(minuto);
        tarjeta.setOrigen(OrigenEvento.MANUAL);
        tarjeta.setEstado(EstadoEvento.CONFIRMADO);
        tarjeta.setJugador(obtenerParticipanteDelPartido(partido, request.jugadorParticipanteId()));
        tarjeta.setMotivo(request.motivo());
        return tarjeta;
    }

    private Jugador obtenerJugadorAutenticado(Authentication authentication) {
        Usuario usuario = securityService.getUsuarioActual(authentication);
        if (!(usuario instanceof Jugador jugador)) {
            throw new OperacionNoPermitidaException("Solo los jugadores pueden realizar esta accion");
        }
        return jugador;
    }

    private void validarParticipanteJugador(Partido partido, Jugador jugador) {
        boolean esParticipante = partido.getParticipantes().stream()
                .anyMatch(p -> p.getPersona() instanceof Jugador j
                        && j.getId().equals(jugador.getId()));
        if (!esParticipante) {
            throw new OperacionNoPermitidaException(
                    "Debe ser participante del partido para realizar esta accion");
        }
    }

    private ParticipantePartido obtenerParticipanteDelPartido(Partido partido, Long participanteId) {
        if (participanteId == null) {
            throw new IllegalArgumentException("Debe indicar el participante");
        }
        return partido.getParticipantes().stream()
                .filter(p -> p.getId().equals(participanteId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Participante", participanteId));
    }
}
