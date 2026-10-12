package com.reservacanchas.service.impl;

import com.reservacanchas.dto.clip.ClipDetalleResponse;
import com.reservacanchas.dto.clip.ClipResponse;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.mapper.ClipMapper;
import com.reservacanchas.model.Clip;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.Grabacion;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.Usuario;
import com.reservacanchas.model.enums.EstadoClip;
import com.reservacanchas.repository.ClipRepository;
import com.reservacanchas.repository.EventoPartidoRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.service.ClipService;
import com.reservacanchas.service.IAlmacenamiento;
import com.reservacanchas.service.IGeneradorClip;
import com.reservacanchas.service.SecurityService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClipServiceImpl implements ClipService {

    private final ClipRepository clipRepository;
    private final PartidoRepository partidoRepository;
    private final EventoPartidoRepository eventoRepository;
    private final IGeneradorClip generadorClip;
    private final IAlmacenamiento almacenamiento;
    private final ClipMapper clipMapper;
    private final SecurityService securityService;

    public ClipServiceImpl(
            ClipRepository clipRepository,
            PartidoRepository partidoRepository,
            EventoPartidoRepository eventoRepository,
            IGeneradorClip generadorClip,
            IAlmacenamiento almacenamiento,
            ClipMapper clipMapper,
            SecurityService securityService) {
        this.clipRepository = clipRepository;
        this.partidoRepository = partidoRepository;
        this.eventoRepository = eventoRepository;
        this.generadorClip = generadorClip;
        this.almacenamiento = almacenamiento;
        this.clipMapper = clipMapper;
        this.securityService = securityService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClipResponse> listarClipsPartido(Long partidoId, Authentication authentication) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Partido", partidoId));
        validarAccesoPartido(partido, authentication);

        List<Clip> clips = clipRepository.findByPartidoIdAndVisibleTrue(partidoId);
        return clips.stream()
                .filter(c -> !c.estaDescartado())
                .map(clipMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ClipDetalleResponse obtenerClip(Long clipId, Authentication authentication) {
        Clip clip = clipRepository.findById(clipId)
                .orElseThrow(() -> new ResourceNotFoundException("Clip", clipId));

        if (clip.estaDescartado()) {
            throw new ResourceNotFoundException("Clip no disponible o descartado: " + clipId);
        }

        validarAccesoPartido(clip.getPartido(), authentication);

        clip.incrementarVistas();
        clipRepository.save(clip);

        String urlFirmada = almacenamiento.generarUrlFirmada(clip.getUrlVideo());
        return clipMapper.toDetalleResponse(clip, urlFirmada);
    }

    @Override
    @Transactional
    public ClipResponse descartarClip(Long clipId, Authentication authentication) {
        Clip clip = clipRepository.findById(clipId)
                .orElseThrow(() -> new ResourceNotFoundException("Clip", clipId));
        validarAccesoPartido(clip.getPartido(), authentication);

        clip.descartar();
        Clip guardado = clipRepository.save(clip);
        return clipMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public ClipResponse reintentarClip(Long clipId, Authentication authentication) {
        Clip clip = clipRepository.findById(clipId)
                .orElseThrow(() -> new ResourceNotFoundException("Clip", clipId));
        validarAccesoPartido(clip.getPartido(), authentication);

        if (clip.getEstado() != EstadoClip.ERROR) {
            throw new OperacionNoPermitidaException("Solo se pueden reintentar clips en estado ERROR");
        }

        clip.setEstado(EstadoClip.PROCESANDO);
        Clip guardado = clipRepository.save(clip);

        // Reejecutar generación de clip de forma asíncrona
        generarClipAsync(guardado.getGrabacion(), null, guardado.getInstante(), guardado.getSegundosAntes(), guardado.getSegundosDespues());

        return clipMapper.toResponse(guardado);
    }

    @Override
    @Async("clipTaskExecutor")
    @Transactional
    public void generarClipAsync(Grabacion grabacion, Gol gol, LocalDateTime instante, int antes, int despues) {
        try {
            Clip nuevoClip = generadorClip.generar(grabacion, instante, antes, despues);
            Clip guardado = clipRepository.save(nuevoClip);

            if (gol != null) {
                gol.setClip(guardado);
                eventoRepository.save(gol);
            }
        } catch (Exception e) {
            if (gol != null && gol.getClip() != null) {
                gol.getClip().marcarError("Excepcion al generar clip: " + e.getMessage());
                clipRepository.save(gol.getClip());
            }
        }
    }

    private void validarAccesoPartido(Partido partido, Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        if (securityService.esAdministrador(authentication)) {
            return;
        }
        if (securityService.esAdministradorSede(authentication)) {
            if (partido != null && partido.getReserva() != null && partido.getReserva().getCancha() != null) {
                try {
                    securityService.verificarGestionSede(partido.getReserva().getCancha().getSede(), authentication);
                    return;
                } catch (Exception e) {
                    throw new AccessDeniedException("No tiene permisos para gestionar la sede de este partido");
                }
            }
            throw new AccessDeniedException("No tiene permisos para este partido");
        }
        Usuario usuario = securityService.getUsuarioActual(authentication);
        if (usuario instanceof Jugador) {
            boolean esParticipante = partido != null && partido.getParticipantes() != null && partido.getParticipantes().stream()
                    .anyMatch(p -> p.getPersona() != null && p.getPersona().getId().equals(usuario.getId()));
            boolean esOrganizador = partido != null && partido.getReserva() != null
                    && partido.getReserva().getOrganizador() != null
                    && partido.getReserva().getOrganizador().getId().equals(usuario.getId());
            if (esParticipante || esOrganizador) {
                return;
            }
        }
        throw new AccessDeniedException("Solo los participantes del partido o administradores de la sede pueden acceder a los clips");
    }
}

