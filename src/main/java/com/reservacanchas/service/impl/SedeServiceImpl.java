package com.reservacanchas.service.impl;

import com.reservacanchas.dto.reserva.ReservaResponse;
import com.reservacanchas.dto.sede.SedeRequest;
import com.reservacanchas.dto.sede.SedeResponse;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.mapper.ReservaMapper;
import com.reservacanchas.mapper.SedeMapper;
import com.reservacanchas.model.AdministradorSede;
import com.reservacanchas.model.Sede;
import com.reservacanchas.model.Usuario;
import com.reservacanchas.repository.AdministradorSedeRepository;
import com.reservacanchas.repository.ReservaRepository;
import com.reservacanchas.repository.SedeRepository;
import com.reservacanchas.service.SecurityService;
import com.reservacanchas.service.SedeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SedeServiceImpl implements SedeService {

    private final SedeRepository sedeRepository;
    private final AdministradorSedeRepository administradorSedeRepository;
    private final SedeMapper sedeMapper;
    private final SecurityService securityService;
    private final ReservaRepository reservaRepository;
    private final ReservaMapper reservaMapper;

    public SedeServiceImpl(SedeRepository sedeRepository,
                           AdministradorSedeRepository administradorSedeRepository,
                           SedeMapper sedeMapper,
                           SecurityService securityService,
                           ReservaRepository reservaRepository,
                           ReservaMapper reservaMapper) {
        this.sedeRepository = sedeRepository;
        this.administradorSedeRepository = administradorSedeRepository;
        this.sedeMapper = sedeMapper;
        this.securityService = securityService;
        this.reservaRepository = reservaRepository;
        this.reservaMapper = reservaMapper;
    }

    @Override
    @Transactional
    public SedeResponse crear(SedeRequest request, Authentication authentication) {
        Sede sede = sedeMapper.toEntity(request);

        Usuario usuario = securityService.getUsuarioActual(authentication);
        if (usuario instanceof AdministradorSede adminSede) {
            sede.setAdministradorSede(adminSede);
        } else {
            if (request.administradorSedeId() == null) {
                throw new IllegalArgumentException("Debe especificar el administrador de sede");
            }
            AdministradorSede adminSede = administradorSedeRepository.findById(request.administradorSedeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "AdministradorSede", request.administradorSedeId()));
            sede.setAdministradorSede(adminSede);
        }

        Sede guardada = sedeRepository.save(sede);
        return sedeMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public SedeResponse obtenerPorId(Long id) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede", id));
        return sedeMapper.toResponse(sede);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SedeResponse> listarTodas() {
        return sedeRepository.findAll().stream()
                .map(sedeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SedeResponse actualizar(Long id, SedeRequest request, Authentication authentication) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede", id));

        securityService.verificarGestionSede(sede, authentication);

        sede.setNombre(request.nombre());
        sede.setDireccion(request.direccion());
        sede.setDistrito(request.distrito());
        sede.setLatitud(request.latitud());
        sede.setLongitud(request.longitud());
        sede.setHoraApertura(request.horaApertura());
        sede.setHoraCierre(request.horaCierre());
        sede.setActiva(request.activa());

        if (securityService.esAdministrador(authentication) && request.administradorSedeId() != null) {
            AdministradorSede adminSede = administradorSedeRepository.findById(request.administradorSedeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "AdministradorSede", request.administradorSedeId()));
            sede.setAdministradorSede(adminSede);
        }

        Sede actualizada = sedeRepository.save(sede);
        return sedeMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Authentication authentication) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede", id));
        securityService.verificarGestionSede(sede, authentication);
        sedeRepository.delete(sede);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SedeResponse> listarMisSedes(Authentication authentication) {
        Usuario usuario = securityService.getUsuarioActual(authentication);
        if (!(usuario instanceof AdministradorSede adminSede)) {
            throw new IllegalArgumentException("Solo los administradores de sede pueden ver sus sedes");
        }
        return sedeRepository.findByAdministradorSedeId(adminSede.getId()).stream()
                .map(sedeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> listarReservasPorSede(Long id, LocalDate fecha, Authentication authentication) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede", id));
        securityService.verificarGestionSede(sede, authentication);
        return reservaRepository.buscarPorSedeYFecha(id, fecha).stream()
                .map(reservaMapper::toResponse)
                .toList();
    }
}
