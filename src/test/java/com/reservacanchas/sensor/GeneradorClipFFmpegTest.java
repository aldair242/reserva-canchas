package com.reservacanchas.sensor;

import com.reservacanchas.model.Clip;
import com.reservacanchas.model.Grabacion;
import com.reservacanchas.model.Intervalo;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.enums.EstadoClip;
import com.reservacanchas.model.enums.EstadoGrabacion;
import com.reservacanchas.service.IAlmacenamiento;
import com.reservacanchas.service.impl.GeneradorClipFFmpeg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneradorClipFFmpegTest {

    @Mock
    private IAlmacenamiento almacenamiento;

    private GeneradorClipFFmpeg generadorClipSimulado;
    private Grabacion grabacion;
    private Partido partido;

    @BeforeEach
    void setUp() {
        generadorClipSimulado = new GeneradorClipFFmpeg(
                almacenamiento,
                "ffmpeg",
                true, // simulado = true
                15,
                8
        );

        partido = new Partido();
        partido.setId(1L);

        grabacion = new Grabacion();
        grabacion.setId(10L);
        grabacion.setPartido(partido);
        grabacion.setInicio(LocalDateTime.now().minusMinutes(10));
        grabacion.setEstado(EstadoGrabacion.GRABANDO);
        grabacion.setUrlBase("rtsp://simulado/stream");
    }

    @Test
    void generar_modoSimulado_creaClipListoSinNecesidadDeFFmpeg() {
        when(almacenamiento.subir(any(File.class)))
                .thenReturn("video_simulado_123.mp4")
                .thenReturn("miniatura_simulada_123.jpg");

        LocalDateTime instanteGol = LocalDateTime.now();
        Clip clip = generadorClipSimulado.generar(grabacion, instanteGol, 15, 8);

        assertNotNull(clip);
        assertEquals(EstadoClip.LISTO, clip.getEstado());
        assertEquals("video_simulado_123.mp4", clip.getUrlVideo());
        assertEquals("miniatura_simulada_123.jpg", clip.getUrlMiniatura());
        assertEquals(15, clip.getSegundosAntes());
        assertEquals(8, clip.getSegundosDespues());
        assertEquals(23, clip.getDuracionSegundos());
        assertTrue(clip.isVisible());
        assertFalse(clip.estaDescartado());

        // Se verifica que subio video y miniatura
        verify(almacenamiento, atLeastOnce()).subir(any(File.class));
    }

    @Test
    void clip_calcularRango_retornaIntervaloCorrecto() {
        LocalDateTime instante = LocalDateTime.of(2026, 10, 11, 15, 30, 0);
        Clip clip = new Clip();
        clip.setInstante(instante);
        clip.setSegundosAntes(15);
        clip.setSegundosDespues(8);

        Intervalo rango = clip.calcularRango();

        assertNotNull(rango);
        assertEquals(instante.minusSeconds(15), rango.inicio());
        assertEquals(instante.plusSeconds(8), rango.fin());
    }

    @Test
    void clip_descartar_cambiaEstadoYVisibilidad() {
        Clip clip = new Clip();
        clip.setEstado(EstadoClip.LISTO);
        clip.setVisible(true);

        assertFalse(clip.estaDescartado());

        clip.descartar();

        assertTrue(clip.estaDescartado());
        assertFalse(clip.isVisible());
        assertEquals(EstadoClip.DESCARTADO, clip.getEstado());
    }
}

