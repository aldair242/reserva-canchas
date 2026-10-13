package com.reservacanchas.service.impl;

import com.reservacanchas.dto.partido.ParticipanteRequest;
import com.reservacanchas.dto.partido.PartidoResponse;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.mapper.PartidoMapper;
import com.reservacanchas.model.Invitado;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.Persona;
import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.Usuario;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.EstadoReserva;
import com.reservacanchas.model.CamaraCancha;
import com.reservacanchas.model.Grabacion;
import com.reservacanchas.model.enums.EstadoGrabacion;
import com.reservacanchas.repository.CamaraCanchaRepository;
import com.reservacanchas.repository.GrabacionRepository;
import com.reservacanchas.repository.InvitadoRepository;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.repository.ReservaRepository;
import com.reservacanchas.service.PartidoService;
import com.reservacanchas.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PartidoServiceImpl implements PartidoService {

    private final PartidoRepository partidoRepository;
    private final ReservaRepository reservaRepository;
    private final JugadorRepository jugadorRepository;
    private final InvitadoRepository invitadoRepository;
    private final CamaraCanchaRepository camaraCanchaRepository;
    private final GrabacionRepository grabacionRepository;
    private final PartidoMapper partidoMapper;
    private final SecurityService securityService;

    public PartidoServiceImpl(PartidoRepository partidoRepository,
                              ReservaRepository reservaRepository,
                              JugadorRepository jugadorRepository,
                              InvitadoRepository invitadoRepository,
                              CamaraCanchaRepository camaraCanchaRepository,
                              GrabacionRepository grabacionRepository,
                              PartidoMapper partidoMapper,
                              SecurityService securityService) {
        this.partidoRepository = partidoRepository;
        this.reservaRepository = reservaRepository;
        this.jugadorRepository = jugadorRepository;
        this.invitadoRepository = invitadoRepository;
        this.camaraCanchaRepository = camaraCanchaRepository;
        this.grabacionRepository = grabacionRepository;
        this.partidoMapper = partidoMapper;
        this.securityService = securityService;
    }

    @Override
    @Transactional
    public PartidoResponse crearDesdeReserva(Long reservaId, Authentication authentication) {
        Usuario usuario = securityService.getUsuarioActual(authentication);
        if (!(usuario instanceof Jugador organizador)) {
            throw new OperacionNoPermitidaException("Solo los jugadores pueden crear partidos");
        }

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", reservaId));

        if (!reserva.getOrganizador().getId().equals(organizador.getId())) {
            throw new OperacionNoPermitidaException("No puede crear un partido de una reserva ajena");
        }
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new OperacionNoPermitidaException(
                    "La reserva debe estar confirmada para crear un partido");
        }
        if (!reserva.estaPagada()) {
            throw new OperacionNoPermitidaException(
                    "La reserva debe estar pagada para crear un partido");
        }
        if (partidoRepository.findByReservaId(reservaId).isPresent()) {
            throw new OperacionNoPermitidaException(
                    "La reserva ya tiene un partido asociado");
        }

        Partido partido = new Partido();
        partido.setReserva(reserva);
        partido.setEstado(EstadoPartido.PROGRAMADO);
        partido.setNombreEquipoA("Equipo A");
        partido.setNombreEquipoB("Equipo B");
        partido.setGolesEquipoA(0);
        partido.setGolesEquipoB(0);
        partido.generarEnlaceInvitacion();

        // El organizador es el primer participante del equipo A
        ParticipantePartido participanteOrg = new ParticipantePartido();
        participanteOrg.setPersona(organizador);
        participanteOrg.setEquipo(com.reservacanchas.model.enums.LadoEquipo.A);
        participanteOrg.setAsistio(true);
        partido.agregarParticipante(participanteOrg);

        Partido guardado = partidoRepository.save(partido);
        return partidoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PartidoResponse obtenerPorId(Long id) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido", id));
        return partidoMapper.toResponse(partido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> listarMisPartidos(Authentication authentication) {
        Usuario usuario = securityService.getUsuarioActual(authentication);
        List<Partido> partidos = partidoRepository.findMisPartidos(usuario.getId());
        return partidos.stream()
                .map(partidoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PartidoResponse agregarParticipante(Long partidoId, ParticipanteRequest request,
                                               Authentication authentication) {
        Usuario usuario = securityService.getUsuarioActual(authentication);
        if (!(usuario instanceof Jugador jugadorActual)) {
            throw new OperacionNoPermitidaException("Solo los jugadores pueden participar o gestionar partidos");
        }

        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Partido", partidoId));

        boolean esOrganizador = partido.getReserva().getOrganizador().getId().equals(jugadorActual.getId());
        boolean esElMismoJugador = request.jugadorId() != null && request.jugadorId().equals(jugadorActual.getId());

        if (!esOrganizador && !esElMismoJugador) {
            throw new OperacionNoPermitidaException(
                    "No tiene permisos para gestionar este partido");
        }

        Persona persona;
        if (request.jugadorId() != null) {
            persona = jugadorRepository.findById(request.jugadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Jugador", request.jugadorId()));
        } else if (request.nombreInvitado() != null && request.apellidoInvitado() != null) {
            Invitado invitado = new Invitado();
            invitado.setNombre(request.nombreInvitado());
            invitado.setApellido(request.apellidoInvitado());
            invitado.setReclamado(false);
            invitado.setTokenReclamo("REC-" + UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 20).toUpperCase());
            persona = invitadoRepository.save(invitado);
        } else {
            throw new IllegalArgumentException(
                    "Debe indicar un jugadorId o un nombre y apellido de invitado");
        }

        // Validar que la persona no esté ya inscrita en el partido
        boolean yaInscrito = partido.getParticipantes().stream()
                .anyMatch(p -> p.getPersona() != null && p.getPersona().getId().equals(persona.getId()));
        if (yaInscrito) {
            throw new OperacionNoPermitidaException("El jugador ya se encuentra registrado en este partido");
        }

        ParticipantePartido participante = new ParticipantePartido();
        participante.setPersona(persona);
        participante.setEquipo(request.equipo());
        participante.setAsistio(false);
        partido.agregarParticipante(participante);

        Partido guardado = partidoRepository.save(partido);
        return partidoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public PartidoResponse iniciar(Long id, Authentication authentication) {
        Partido partido = obtenerPartidoPropio(id, authentication);
        if (partido.getEstado() != EstadoPartido.PROGRAMADO) {
            throw new OperacionNoPermitidaException(
                    "El partido no puede iniciarse en estado " + partido.getEstado());
        }
        partido.iniciar();
        Partido guardado = partidoRepository.save(partido);

        // Al iniciar un Partido se crea una Grabacion en estado GRABANDO
        if (guardado.getReserva() != null && guardado.getReserva().getCancha() != null) {
            Long canchaId = guardado.getReserva().getCancha().getId();
            CamaraCancha camara = camaraCanchaRepository.findFirstByCanchaIdAndActivoTrue(canchaId)
                    .orElse(null);
            if (camara != null) {
                Grabacion grabacion = camara.iniciarGrabacion(guardado);
                grabacionRepository.save(grabacion);
            }
        }

        return partidoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public PartidoResponse finalizar(Long id, Authentication authentication) {
        Partido partido = obtenerPartidoPropio(id, authentication);
        if (partido.getEstado() != EstadoPartido.EN_JUEGO) {
            throw new OperacionNoPermitidaException(
                    "El partido no puede finalizarse en estado " + partido.getEstado());
        }
        partido.finalizar();
        Partido guardado = partidoRepository.save(partido);

        // Al finalizarlo pasa a FINALIZADA
        List<Grabacion> grabacionesActivas = grabacionRepository
                .findByPartidoIdAndEstado(guardado.getId(), EstadoGrabacion.GRABANDO);
        for (Grabacion grabacion : grabacionesActivas) {
            grabacion.finalizar();
            grabacionRepository.save(grabacion);
        }

        if (guardado.getReserva() != null && guardado.getReserva().getEstado() == EstadoReserva.CONFIRMADA) {
            guardado.getReserva().completar();
            reservaRepository.save(guardado.getReserva());
        }

        return partidoMapper.toResponse(guardado);
    }

    private Partido obtenerPartidoPropio(Long id, Authentication authentication) {
        Usuario usuario = securityService.getUsuarioActual(authentication);
        if (!(usuario instanceof Jugador organizador)) {
            throw new OperacionNoPermitidaException("Solo los jugadores pueden gestionar partidos");
        }
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido", id));
        if (!partido.getReserva().getOrganizador().getId().equals(organizador.getId())) {
            throw new OperacionNoPermitidaException(
                    "No tiene permisos para gestionar este partido");
        }
        return partido;
    }
}
