package com.reservacanchas.service.impl;

import com.reservacanchas.model.Clip;
import com.reservacanchas.model.Grabacion;
import com.reservacanchas.model.enums.EstadoClip;
import com.reservacanchas.service.IAlmacenamiento;
import com.reservacanchas.service.IGeneradorClip;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class GeneradorClipFFmpeg implements IGeneradorClip {

    private final IAlmacenamiento almacenamiento;
    private final String rutaFFmpeg;
    private final boolean simulado;
    private final int defaultSegundosAntes;
    private final int defaultSegundosDespues;

    public GeneradorClipFFmpeg(
            IAlmacenamiento almacenamiento,
            @Value("${clips.ffmpeg.ruta:ffmpeg}") String rutaFFmpeg,
            @Value("${clips.ffmpeg.simulado:true}") boolean simulado,
            @Value("${clips.segundos-antes:15}") int defaultSegundosAntes,
            @Value("${clips.segundos-despues:8}") int defaultSegundosDespues) {
        this.almacenamiento = almacenamiento;
        this.rutaFFmpeg = rutaFFmpeg;
        this.simulado = simulado;
        this.defaultSegundosAntes = defaultSegundosAntes;
        this.defaultSegundosDespues = defaultSegundosDespues;
    }

    public String getRutaFFmpeg() {
        return rutaFFmpeg;
    }

    public boolean isSimulado() {
        return simulado;
    }

    @Override
    public Clip generar(Grabacion g, LocalDateTime instante, int antes, int despues) {
        int segAntes = antes > 0 ? antes : defaultSegundosAntes;
        int segDespues = despues > 0 ? despues : defaultSegundosDespues;

        Clip clip = new Clip();
        clip.setGrabacion(g);
        if (g != null) {
            clip.setPartido(g.getPartido());
        }
        clip.setInstante(instante);
        clip.setSegundosAntes(segAntes);
        clip.setSegundosDespues(segDespues);
        clip.setDuracionSegundos(segAntes + segDespues);
        clip.setEstado(EstadoClip.PROCESANDO);
        clip.setVisible(true);
        clip.setVistas(0);

        if (simulado) {
            try {
                File dummyVideo = File.createTempFile("clip_simulado_", ".mp4");
                Files.writeString(dummyVideo.toPath(), "SIMULATED_CLIP_VIDEO_DATA_" + (g != null ? g.getId() : "0") + "_" + instante);
                File dummyThumb = File.createTempFile("thumb_simulado_", ".jpg");
                Files.writeString(dummyThumb.toPath(), "SIMULATED_THUMBNAIL_DATA");

                String urlVideo = almacenamiento.subir(dummyVideo);
                String urlThumb = almacenamiento.subir(dummyThumb);

                clip.marcarListo(urlVideo, urlThumb);

                dummyVideo.delete();
                dummyThumb.delete();
            } catch (IOException e) {
                clip.marcarError("Error al generar clip simulado: " + e.getMessage());
            }
            return clip;
        }

        // Modo real con ProcessBuilder
        try {
            long offset = 0;
            if (g != null && g.getInicio() != null && instante != null) {
                offset = Math.max(0, Duration.between(g.getInicio(), instante).toSeconds() - segAntes);
            }
            int duracion = segAntes + segDespues;
            File tempVideo = File.createTempFile("clip_ffmpeg_", ".mp4");

            ProcessBuilder pb = new ProcessBuilder(
                    rutaFFmpeg, "-y",
                    "-ss", String.valueOf(offset),
                    "-i", (g != null && g.getUrlBase() != null) ? g.getUrlBase() : "input.mp4",
                    "-t", String.valueOf(duracion),
                    "-c", "copy",
                    tempVideo.getAbsolutePath()
            );
            Process process = pb.start();
            boolean terminado = process.waitFor(30, TimeUnit.SECONDS);

            if (terminado && process.exitValue() == 0) {
                String urlVideo = almacenamiento.subir(tempVideo);
                clip.marcarListo(urlVideo, urlVideo + ".jpg");
            } else {
                clip.marcarError("FFmpeg finalizo con codigo " + (terminado ? process.exitValue() : "TIMEOUT"));
            }
            tempVideo.delete();
        } catch (Exception e) {
            clip.marcarError("Error ejecutando FFmpeg: " + e.getMessage());
        }

        return clip;
    }
}

