package com.reservacanchas.service.impl;

import com.reservacanchas.dto.auth.AuthResponse;
import com.reservacanchas.dto.auth.LoginRequest;
import com.reservacanchas.dto.auth.RegistroRequest;
import com.reservacanchas.dto.auth.UsuarioAuthResponse;
import com.reservacanchas.exception.CredencialesInvalidasException;
import com.reservacanchas.model.Administrador;
import com.reservacanchas.model.AdministradorSede;
import com.reservacanchas.model.EstadisticaJugador;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.Usuario;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.repository.UsuarioRepository;
import com.reservacanchas.security.JwtService;
import com.reservacanchas.security.UsuarioDetails;
import com.reservacanchas.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    public AuthServiceImpl(JugadorRepository jugadorRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.jugadorRepository = jugadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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

        return new AuthResponse(token, "Bearer", toAuthResponse(guardado));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.correo(), request.password()));

            UsuarioDetails userDetails = (UsuarioDetails) authentication.getPrincipal();
            Usuario usuario = usuarioRepository.findByCorreo(request.correo())
                    .orElseThrow(CredencialesInvalidasException::new);

            String token = jwtService.generateToken(userDetails);
            return new AuthResponse(token, "Bearer", toAuthResponse(usuario));
        } catch (AuthenticationException | CredencialesInvalidasException e) {
            throw new CredencialesInvalidasException();
        }
    }

    private UsuarioAuthResponse toAuthResponse(Usuario usuario) {
        String rol = "JUGADOR";
        String apodo = null;
        String fotoUrl = null;
        String posicionPreferida = null;

        if (usuario instanceof Administrador) {
            rol = "ADMIN";
        } else if (usuario instanceof AdministradorSede) {
            rol = "ADMIN_SEDE";
        } else if (usuario instanceof Jugador jugador) {
            apodo = jugador.getApodo();
            fotoUrl = jugador.getFotoUrl();
            posicionPreferida = jugador.getPosicionPreferida();
        }

        return new UsuarioAuthResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                rol,
                apodo,
                fotoUrl,
                posicionPreferida
        );
    }
}
