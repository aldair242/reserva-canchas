package com.reservacanchas.service;

import com.reservacanchas.dto.JugadorResponse;
import com.reservacanchas.dto.auth.AuthResponse;
import com.reservacanchas.dto.auth.LoginRequest;
import com.reservacanchas.dto.auth.RegistroRequest;
import com.reservacanchas.mapper.JugadorMapper;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.repository.UsuarioRepository;
import com.reservacanchas.security.JwtService;
import com.reservacanchas.security.UsuarioDetails;
import com.reservacanchas.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private JugadorMapper jugadorMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegistroRequest registroRequest;
    private LoginRequest loginRequest;
    private Jugador jugador;

    @BeforeEach
    void setUp() {
        registroRequest = new RegistroRequest(
                "Juan", "Perez", "123456789",
                "juan@example.com", "password123",
                "juancho", "http://foto.jpg", "Delantero"
        );

        loginRequest = new LoginRequest("juan@example.com", "password123");

        jugador = new Jugador();
        jugador.setId(1L);
        jugador.setNombre("Juan");
        jugador.setApellido("Perez");
        jugador.setCorreo("juan@example.com");
        jugador.setPasswordHash("encodedPassword");
        jugador.setFechaRegistro(LocalDateTime.now());
        jugador.setActivo(true);
        jugador.setApodo("juancho");
    }

    @Test
    void registrar_debeCrearJugadorYDevolverToken() {
        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jugadorRepository.save(any(Jugador.class))).thenReturn(jugador);
        when(jwtService.generateToken(any(UsuarioDetails.class))).thenReturn("jwt-token");
        when(jugadorMapper.toResponse(any(Jugador.class))).thenReturn(
                new JugadorResponse(1L, "Juan", "Perez", "123456789",
                        "juan@example.com", "juancho", "http://foto.jpg", "Delantero", jugador.getFechaRegistro())
        );

        AuthResponse response = authService.registrar(registroRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.tipo());
        assertNotNull(response.jugador());
        assertEquals("juan@example.com", response.jugador().correo());
        verify(jugadorRepository).save(any(Jugador.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void registrar_conCorreoExistente_debeLanzarExcepcion() {
        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registrar(registroRequest));
        verify(jugadorRepository, never()).save(any(Jugador.class));
    }

    @Test
    void login_debeAutenticarYDevolverToken() {
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        UsuarioDetails userDetails = new UsuarioDetails(jugador);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(any(UsuarioDetails.class))).thenReturn("jwt-token");
        when(jugadorRepository.findByCorreo(anyString())).thenReturn(Optional.of(jugador));
        when(jugadorMapper.toResponse(any(Jugador.class))).thenReturn(
                new JugadorResponse(1L, "Juan", "Perez", "123456789",
                        "juan@example.com", "juancho", null, "Delantero", jugador.getFechaRegistro())
        );

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("juan@example.com", response.jugador().correo());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
