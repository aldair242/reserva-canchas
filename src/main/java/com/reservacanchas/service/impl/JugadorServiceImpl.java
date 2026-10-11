package com.reservacanchas.service.impl;

import com.reservacanchas.dto.JugadorResponse;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.mapper.JugadorMapper;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.security.UsuarioDetails;
import com.reservacanchas.service.JugadorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JugadorServiceImpl implements JugadorService {

    private final JugadorRepository jugadorRepository;
    private final JugadorMapper jugadorMapper;

    public JugadorServiceImpl(JugadorRepository jugadorRepository, JugadorMapper jugadorMapper) {
        this.jugadorRepository = jugadorRepository;
        this.jugadorMapper = jugadorMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public JugadorResponse obtenerPorId(Long id) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador", id));
        return jugadorMapper.toResponse(jugador);
    }

    @Override
    @Transactional(readOnly = true)
    public JugadorResponse obtenerPerfilActual(Authentication authentication) {
        UsuarioDetails userDetails = (UsuarioDetails) authentication.getPrincipal();
        String correo = userDetails.getUsername();
        Jugador jugador = jugadorRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado con correo: " + correo));
        return jugadorMapper.toResponse(jugador);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> listarTodos() {
        return jugadorRepository.findAll()
                .stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }
}
