package com.reservacanchas.service;

import com.reservacanchas.dto.auth.AuthResponse;
import com.reservacanchas.dto.auth.LoginRequest;
import com.reservacanchas.dto.auth.RegistroRequest;
import com.reservacanchas.dto.auth.UsuarioAuthResponse;
import com.reservacanchas.exception.CredencialesInvalidasException;
import com.reservacanchas.model.AdministradorSede;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        jugador.setTelefono("123456789");
        jugador.setCorreo("juan@example.com");
        jugador.setPasswordHash("encodedPassword");
        jugador.setFechaRegistro(LocalDateTime.now());
        jugador.setActivo(true);
        jugador.setApodo("juancho");
        jugador.setFotoUrl("http://foto.jpg");
        jugador.setPosicionPreferida("Delantero");
    }

    @Test
    void registrar_debeCrearJugadorYDevolverToken() {
        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jugadorRepository.save(any(Jugador.class))).thenReturn(jugador);
        when(jwtService.generateToken(any(UsuarioDetails.class))).thenReturn("jwt-token");

        AuthResponse response = authService.registrar(registroRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.tipo());

        UsuarioAuthResponse usuario = response.usuario();
        assertNotNull(usuario);
        assertEquals(1L, usuario.id());
        assertEquals("Juan", usuario.nombre());
        assertEquals("Perez", usuario.apellido());
        assertEquals("juan@example.com", usuario.correo());
        assertEquals("123456789", usuario.telefono());
        assertEquals("JUGADOR", usuario.rol());
        assertEquals("juancho", usuario.apodo());
        assertEquals("http://foto.jpg", usuario.fotoUrl());
        assertEquals("Delantero", usuario.posicionPreferida());

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
    void login_jugador_debeAutenticarYDevolverToken() {
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        UsuarioDetails userDetails = new UsuarioDetails(jugador);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(any(UsuarioDetails.class))).thenReturn("jwt-token");
        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.of(jugador));

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());

        UsuarioAuthResponse usuario = response.usuario();
        assertNotNull(usuario);
        assertEquals("juan@example.com", usuario.correo());
        assertEquals("JUGADOR", usuario.rol());
        assertEquals("juancho", usuario.apodo());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_administradorSede_debeAutenticarYDevolverRol() {
        AdministradorSede adminSede = new AdministradorSede();
        adminSede.setId(2L);
        adminSede.setNombre("Admin");
        adminSede.setApellido("Sede");
        adminSede.setTelefono("999999999");
        adminSede.setCorreo("admin@prueba.com");
        adminSede.setPasswordHash("encodedPassword");
        adminSede.setFechaRegistro(LocalDateTime.now());
        adminSede.setActivo(true);

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        UsuarioDetails userDetails = new UsuarioDetails(adminSede);
        LoginRequest req = new LoginRequest("admin@prueba.com", "admin123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(any(UsuarioDetails.class))).thenReturn("jwt-token");
        when(usuarioRepository.findByCorreo("admin@prueba.com")).thenReturn(Optional.of(adminSede));

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());

        UsuarioAuthResponse usuario = response.usuario();
        assertNotNull(usuario);
        assertEquals("admin@prueba.com", usuario.correo());
        assertEquals("ADMIN_SEDE", usuario.rol());
        assertNull(usuario.apodo());
        assertNull(usuario.fotoUrl());
        assertNull(usuario.posicionPreferida());
    }

    @Test
    void login_credencialesInvalidas_debeLanzarCredencialesInvalidasException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        CredencialesInvalidasException ex = assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.login(loginRequest));

        assertEquals("Credenciales inválidas", ex.getMessage());
    }
}
