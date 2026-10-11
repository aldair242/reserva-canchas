package com.reservacanchas.service.impl;

import com.reservacanchas.dto.JugadorResponse;
import com.reservacanchas.dto.auth.AuthResponse;
import com.reservacanchas.dto.auth.LoginRequest;
import com.reservacanchas.dto.auth.RegistroRequest;
import com.reservacanchas.mapper.JugadorMapper;
import com.reservacanchas.model.EstadisticaJugador;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.repository.UsuarioRepository;
import com.reservacanchas.security.JwtService;
import com.reservacanchas.security.UsuarioDetails;
import com.reservacanchas.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final JugadorRepository jugadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JugadorMapper jugadorMapper;

    public AuthServiceImpl(JugadorRepository jugadorRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           JugadorMapper jugadorMapper) {
        this.jugadorRepository = jugadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jugadorMapper = jugadorMapper;
    }

    @Override
    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + request.correo());
        }

        Jugador jugador = new Jugador();
        jugador.setNombre(request.nombre());
        jugador.setApellido(request.apellido());
        jugador.setTelefono(request.telefono());
        jugador.setCorreo(request.correo());
        jugador.setPasswordHash(passwordEncoder.encode(request.password()));
        jugador.setFechaRegistro(LocalDateTime.now());
        jugador.setActivo(true);
        jugador.setApodo(request.apodo());
        jugador.setFotoUrl(request.fotoUrl());
        jugador.setPosicionPreferida(request.posicionPreferida());

        EstadisticaJugador estadistica = new EstadisticaJugador();
        jugador.setEstadistica(estadistica);

        Jugador guardado = jugadorRepository.save(jugador);

        UsuarioDetails userDetails = new UsuarioDetails(guardado);
        String token = jwtService.generateToken(userDetails);

        JugadorResponse jugadorResponse = jugadorMapper.toResponse(guardado);
        return new AuthResponse(token, "Bearer", jugadorResponse);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.correo(), request.password()));

        UsuarioDetails userDetails = (UsuarioDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        Jugador jugador = jugadorRepository.findByCorreo(request.correo())
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado"));

        JugadorResponse jugadorResponse = jugadorMapper.toResponse(jugador);
        return new AuthResponse(token, "Bearer", jugadorResponse);
    }
}
