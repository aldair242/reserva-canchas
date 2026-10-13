package com.reservacanchas.service;

import com.reservacanchas.dto.reserva.ReservaResponse;
import com.reservacanchas.dto.sede.SedeRequest;
import com.reservacanchas.dto.sede.SedeResponse;
import com.reservacanchas.mapper.ReservaMapper;
import com.reservacanchas.mapper.SedeMapper;
import com.reservacanchas.model.Administrador;
import com.reservacanchas.model.AdministradorSede;
import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.Sede;
import com.reservacanchas.repository.AdministradorSedeRepository;
import com.reservacanchas.repository.ReservaRepository;
import com.reservacanchas.repository.SedeRepository;
import com.reservacanchas.service.impl.SedeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SedeServiceAdjustmentTest {

    @Mock
    private SedeRepository sedeRepository;

    @Mock
    private AdministradorSedeRepository administradorSedeRepository;

    @Mock
    private SedeMapper sedeMapper;

    @Mock
    private SecurityService securityService;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ReservaMapper reservaMapper;

    @Mock
    private Authentication authentication;

    private SedeServiceImpl sedeService;

    @BeforeEach
    void setUp() {
        sedeService = new SedeServiceImpl(
                sedeRepository,
                administradorSedeRepository,
                sedeMapper,
                securityService,
                reservaRepository,
                reservaMapper
        );
    }

    @Test
    void listarReservasPorSede_validaSeguridadYRetornaReservas() {
        Long sedeId = 5L;
        LocalDate fecha = LocalDate.of(2026, 10, 25);
        Sede sede = new Sede();
        sede.setId(sedeId);

        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede));

        Reserva reserva = new Reserva();
        reserva.setId(100L);
        when(reservaRepository.buscarPorSedeYFecha(sedeId, fecha)).thenReturn(List.of(reserva));

        ReservaResponse responseMock = new ReservaResponse(
                100L, "RES-100", fecha, LocalTime.of(18, 0), LocalTime.of(19, 0),
                null, null, null, 1L, "Jugador", 2L, "Cancha 1", false, null
        );
        when(reservaMapper.toResponse(reserva)).thenReturn(responseMock);

        List<ReservaResponse> resultado = sedeService.listarReservasPorSede(sedeId, fecha, authentication);

        verify(securityService).verificarGestionSede(sede, authentication);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).id());
    }

    @Test
    void listarReservasPorSede_conFechaNula_retornaTodasLasReservas() {
        Long sedeId = 5L;
        Sede sede = new Sede();
        sede.setId(sedeId);

        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede));

        Reserva reserva = new Reserva();
        reserva.setId(200L);
        when(reservaRepository.buscarPorSedeYFecha(sedeId, null)).thenReturn(List.of(reserva));

        ReservaResponse responseMock = new ReservaResponse(
                200L, "RES-200", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0),
                null, null, null, 1L, "Jugador", 2L, "Cancha 1", false, null
        );
        when(reservaMapper.toResponse(reserva)).thenReturn(responseMock);

        List<ReservaResponse> resultado = sedeService.listarReservasPorSede(sedeId, null, authentication);

        verify(securityService).verificarGestionSede(sede, authentication);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(200L, resultado.get(0).id());
    }

    @Test
    void crear_cuandoEsAdministradorSede_ignoraIdDelCuerpoYAsignaUsuarioAutenticado() {
        AdministradorSede adminAutenticado = new AdministradorSede();
        adminAutenticado.setId(99L);

        when(securityService.getUsuarioActual(authentication)).thenReturn(adminAutenticado);

        SedeRequest request = new SedeRequest(
                "Sede Norte", "Calle 1", "Lima", -12.0, -77.0,
                LocalTime.of(8, 0), LocalTime.of(22, 0), true, 123L
        );

        Sede sedeEntidad = new Sede();
        when(sedeMapper.toEntity(request)).thenReturn(sedeEntidad);
        when(sedeRepository.save(sedeEntidad)).thenAnswer(inv -> inv.getArgument(0));

        SedeResponse responseMock = new SedeResponse(
                1L, "Sede Norte", "Calle 1", "Lima", -12.0, -77.0,
                LocalTime.of(8, 0), LocalTime.of(22, 0), true, 99L
        );
        when(sedeMapper.toResponse(any(Sede.class))).thenReturn(responseMock);

        SedeResponse res = sedeService.crear(request, authentication);

        assertNotNull(res);
        assertEquals(adminAutenticado, sedeEntidad.getAdministradorSede());
    }

    @Test
    void actualizar_cuandoEsAdministradorSede_ignoraCambioDeDueno() {
        Long sedeId = 1L;
        AdministradorSede duenoOriginal = new AdministradorSede();
        duenoOriginal.setId(10L);

        Sede sedeExistente = new Sede();
        sedeExistente.setId(sedeId);
        sedeExistente.setAdministradorSede(duenoOriginal);

        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sedeExistente));
        when(securityService.esAdministrador(authentication)).thenReturn(false);

        SedeRequest request = new SedeRequest(
                "Sede Actualizada", "Calle 2", "Lima", -12.0, -77.0,
                LocalTime.of(8, 0), LocalTime.of(22, 0), true, 999L
        );
        when(sedeRepository.save(any(Sede.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sedeMapper.toResponse(any(Sede.class))).thenReturn(null);

        sedeService.actualizar(sedeId, request, authentication);

        assertEquals(duenoOriginal, sedeExistente.getAdministradorSede());
    }

    @Test
    void actualizar_cuandoEsAdmin_permiteCambiarDueno() {
        Long sedeId = 1L;
        AdministradorSede duenoOriginal = new AdministradorSede();
        duenoOriginal.setId(10L);

        AdministradorSede nuevoDueno = new AdministradorSede();
        nuevoDueno.setId(20L);

        Sede sedeExistente = new Sede();
        sedeExistente.setId(sedeId);
        sedeExistente.setAdministradorSede(duenoOriginal);

        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sedeExistente));
        when(securityService.esAdministrador(authentication)).thenReturn(true);
        when(administradorSedeRepository.findById(20L)).thenReturn(Optional.of(nuevoDueno));

        SedeRequest request = new SedeRequest(
                "Sede Actualizada", "Calle 2", "Lima", -12.0, -77.0,
                LocalTime.of(8, 0), LocalTime.of(22, 0), true, 20L
        );
        when(sedeRepository.save(any(Sede.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sedeMapper.toResponse(any(Sede.class))).thenReturn(null);

        sedeService.actualizar(sedeId, request, authentication);

        assertEquals(nuevoDueno, sedeExistente.getAdministradorSede());
    }
}
