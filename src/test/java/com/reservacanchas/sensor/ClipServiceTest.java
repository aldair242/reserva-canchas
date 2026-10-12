package com.reservacanchas.sensor;

import com.reservacanchas.dto.clip.ClipDetalleResponse;
import com.reservacanchas.dto.clip.ClipResponse;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.mapper.ClipMapper;
import com.reservacanchas.model.Clip;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.enums.EstadoClip;
import com.reservacanchas.repository.ClipRepository;
import com.reservacanchas.repository.EventoPartidoRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.service.IAlmacenamiento;
import com.reservacanchas.service.IGeneradorClip;
import com.reservacanchas.service.SecurityService;
import com.reservacanchas.service.impl.ClipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClipServiceTest {

    @Mock
    private ClipRepository clipRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private EventoPartidoRepository eventoRepository;

    @Mock
    private IGeneradorClip generadorClip;

    @Mock
    private IAlmacenamiento almacenamiento;

    @Mock
    private ClipMapper clipMapper;

    @Mock
    private SecurityService securityService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ClipServiceImpl clipService;

    private Partido partido;
    private Clip clipListo;
    private Clip clipError;

    @BeforeEach
    void setUp() {
        partido = new Partido();
        partido.setId(10L);

        clipListo = new Clip();
        clipListo.setId(100L);
        clipListo.setPartido(partido);
        clipListo.setEstado(EstadoClip.LISTO);
        clipListo.setVisible(true);
        clipListo.setUrlVideo("video100.mp4");
        clipListo.setVistas(0);

        clipError = new Clip();
        clipError.setId(200L);
        clipError.setPartido(partido);
        clipError.setEstado(EstadoClip.ERROR);
        clipError.setVisible(true);
    }

    @Test
    void listarClipsPartido_retornaSoloNoDescartados() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));
        when(securityService.esAdministrador(authentication)).thenReturn(true);

        Clip clipDescartado = new Clip();
        clipDescartado.setId(101L);
        clipDescartado.descartar();

        when(clipRepository.findByPartidoIdAndVisibleTrue(10L)).thenReturn(List.of(clipListo, clipDescartado));
        when(clipMapper.toResponse(clipListo)).thenReturn(new ClipResponse(
                100L, "video100.mp4", "thumb.jpg", 15, 8, 23, null,
                EstadoClip.LISTO, true, 0, 10L, 1L
        ));

        List<ClipResponse> resultado = clipService.listarClipsPartido(10L, authentication);

        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).id());
    }

    @Test
    void obtenerClip_incrementaVistasYGeneraUrlFirmada() {
        when(clipRepository.findById(100L)).thenReturn(Optional.of(clipListo));
        when(securityService.esAdministrador(authentication)).thenReturn(true);
        when(almacenamiento.generarUrlFirmada("video100.mp4")).thenReturn("http://firmada/video100.mp4");

        ClipDetalleResponse detalle = new ClipDetalleResponse(
                100L, "video100.mp4", "http://firmada/video100.mp4", "thumb.jpg",
                15, 8, 23, null, EstadoClip.LISTO, true, 1, 10L, 1L
        );
        when(clipMapper.toDetalleResponse(clipListo, "http://firmada/video100.mp4")).thenReturn(detalle);

        ClipDetalleResponse response = clipService.obtenerClip(100L, authentication);

        assertNotNull(response);
        assertEquals("http://firmada/video100.mp4", response.urlVideoFirmada());
        assertEquals(1, clipListo.getVistas());
        verify(clipRepository).save(clipListo);
    }

    @Test
    void obtenerClip_descartado_lanzaNotFoundException() {
        clipListo.descartar();
        when(clipRepository.findById(100L)).thenReturn(Optional.of(clipListo));

        assertThrows(ResourceNotFoundException.class, () -> clipService.obtenerClip(100L, authentication));
    }

    @Test
    void obtenerClip_sinAutenticacion_lanzaAccessDeniedException() {
        when(clipRepository.findById(100L)).thenReturn(Optional.of(clipListo));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> clipService.obtenerClip(100L, null));
    }

    @Test
    void listarClips_noParticipanteNiAdmin_lanzaAccessDeniedException() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));
        when(securityService.esAdministrador(authentication)).thenReturn(false);
        when(securityService.esAdministradorSede(authentication)).thenReturn(false);

        com.reservacanchas.model.Jugador ajeno = new com.reservacanchas.model.Jugador();
        ajeno.setId(999L);
        when(securityService.getUsuarioActual(authentication)).thenReturn(ajeno);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> clipService.listarClipsPartido(10L, authentication));
    }

    @Test
    void descartarClip_marcaComoDescartado() {
        when(clipRepository.findById(100L)).thenReturn(Optional.of(clipListo));
        when(securityService.esAdministrador(authentication)).thenReturn(true);
        when(clipRepository.save(clipListo)).thenReturn(clipListo);

        ClipResponse responseEsperado = new ClipResponse(
                100L, "video100.mp4", "thumb.jpg", 15, 8, 23, null,
                EstadoClip.DESCARTADO, false, 0, 10L, 1L
        );
        when(clipMapper.toResponse(clipListo)).thenReturn(responseEsperado);

        ClipResponse response = clipService.descartarClip(100L, authentication);

        assertNotNull(response);
        assertTrue(clipListo.estaDescartado());
        assertFalse(clipListo.isVisible());
        verify(clipRepository).save(clipListo);
    }

    @Test
    void reintentarClip_noEnError_lanzaOperacionNoPermitida() {
        when(clipRepository.findById(100L)).thenReturn(Optional.of(clipListo));
        when(securityService.esAdministrador(authentication)).thenReturn(true);

        assertThrows(OperacionNoPermitidaException.class, () -> clipService.reintentarClip(100L, authentication));
    }

    @Test
    void reintentarClip_enError_cambiaAProcesando() {
        when(clipRepository.findById(200L)).thenReturn(Optional.of(clipError));
        when(securityService.esAdministrador(authentication)).thenReturn(true);
        when(clipRepository.save(clipError)).thenReturn(clipError);

        ClipResponse responseEsperado = new ClipResponse(
                200L, null, null, 15, 8, 23, null,
                EstadoClip.PROCESANDO, true, 0, 10L, 1L
        );
        when(clipMapper.toResponse(clipError)).thenReturn(responseEsperado);

        ClipResponse response = clipService.reintentarClip(200L, authentication);

        assertNotNull(response);
        assertEquals(EstadoClip.PROCESANDO, clipError.getEstado());
        verify(clipRepository).save(clipError);
    }
}

