package com.reservacanchas.partido;

import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.model.Invitado;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.Usuario;
import com.reservacanchas.repository.InvitadoRepository;
import com.reservacanchas.repository.ParticipantePartidoRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.service.SecurityService;
import com.reservacanchas.service.impl.InvitacionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReclamoPerfilInvitadoTest {

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private InvitadoRepository invitadoRepository;

    @Mock
    private ParticipantePartidoRepository participanteRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InvitacionServiceImpl invitacionService;

    @Test
    void reclamarPerfil_invitadoNoReclamado_transfiereParticipaciones() {
        Invitado invitado = new Invitado();
        invitado.setId(10L);
        invitado.setNombre("Invitado");
        invitado.setApellido("Test");
        invitado.setReclamado(false);
        invitado.setTokenReclamo("REC-TOKEN");

        Jugador jugador = new Jugador();
        jugador.setId(1L);
        jugador.setNombre("Jugador");
        jugador.setApellido("Real");

        when(invitadoRepository.findByTokenReclamo("REC-TOKEN")).thenReturn(Optional.of(invitado));
        when(securityService.getUsuarioActual(authentication)).thenReturn(jugador);
        when(participanteRepository.reemplazarPersona(eq(invitado), eq(jugador))).thenReturn(3);

        invitacionService.reclamarPerfil("REC-TOKEN", authentication);

        assertTrue(invitado.isReclamado());
        assertEquals(jugador, invitado.getReclamadoPor());
        verify(participanteRepository).reemplazarPersona(eq(invitado), eq(jugador));
        verify(invitadoRepository).save(invitado);
    }

    @Test
    void reclamarPerfil_invitadoYaReclamado_lanzaExcepcion() {
        Invitado invitado = new Invitado();
        invitado.setId(10L);
        invitado.setReclamado(true);
        invitado.setTokenReclamo("REC-TOKEN");

        Jugador jugador = new Jugador();
        jugador.setId(1L);

        when(invitadoRepository.findByTokenReclamo("REC-TOKEN")).thenReturn(Optional.of(invitado));
        when(securityService.getUsuarioActual(authentication)).thenReturn(jugador);

        assertThrows(OperacionNoPermitidaException.class,
                () -> invitacionService.reclamarPerfil("REC-TOKEN", authentication));

        verify(participanteRepository, never()).reemplazarPersona(any(), any());
    }

    @Test
    void reclamarPerfil_noEsJugador_lanzaExcepcion() {
        Usuario admin = new com.reservacanchas.model.Administrador();
        admin.setId(99L);

        when(securityService.getUsuarioActual(authentication)).thenReturn(admin);

        assertThrows(OperacionNoPermitidaException.class,
                () -> invitacionService.reclamarPerfil("REC-TOKEN", authentication));
    }
}
