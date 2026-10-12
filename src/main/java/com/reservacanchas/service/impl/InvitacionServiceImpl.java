package com.reservacanchas.service.impl;

import com.reservacanchas.dto.partido.InvitacionResponse;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.model.Invitado;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.Usuario;
import com.reservacanchas.repository.InvitadoRepository;
import com.reservacanchas.repository.ParticipantePartidoRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.service.InvitacionService;
import com.reservacanchas.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitacionServiceImpl implements InvitacionService {

    private final PartidoRepository partidoRepository;
    private final InvitadoRepository invitadoRepository;
    private final ParticipantePartidoRepository participanteRepository;
    private final SecurityService securityService;

    public InvitacionServiceImpl(PartidoRepository partidoRepository,
                                 InvitadoRepository invitadoRepository,
                                 ParticipantePartidoRepository participanteRepository,
                                 SecurityService securityService) {
        this.partidoRepository = partidoRepository;
        this.invitadoRepository = invitadoRepository;
        this.participanteRepository = participanteRepository;
        this.securityService = securityService;
    }

    @Override
    @Transactional(readOnly = true)
    public InvitacionResponse obtenerResumen(String token) {
        Partido partido = partidoRepository.findByTokenInvitacion(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitacion", token));

        return new InvitacionResponse(
                partido.getId(),
                partido.getEstado(),
                partido.getNombreEquipoA(),
                partido.getNombreEquipoB(),
                partido.getGolesEquipoA(),
                partido.getGolesEquipoB(),
                partido.getReserva().getFecha(),
                partido.getReserva().getHoraInicio(),
                partido.getReserva().getHoraFin(),
                partido.getReserva().getCancha().getNombre(),
                partido.getReserva().getCancha().getSede().getNombre()
        );
    }

    @Override
    @Transactional
    public void reclamarPerfil(String token, Authentication authentication) {
        Usuario usuario = securityService.getUsuarioActual(authentication);
        if (!(usuario instanceof Jugador jugador)) {
            throw new OperacionNoPermitidaException("Solo los jugadores pueden reclamar un perfil");
        }

        Invitado invitado = invitadoRepository.findByTokenReclamo(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitado", token));

        if (invitado.isReclamado()) {
            throw new OperacionNoPermitidaException("Este invitado ya fue reclamado");
        }

        // Reemplazar la referencia al Invitado por el Jugador en todas las participaciones
        participanteRepository.reemplazarPersona(invitado, jugador);

        invitado.reclamarPerfil(jugador);
        invitadoRepository.save(invitado);
    }
}
