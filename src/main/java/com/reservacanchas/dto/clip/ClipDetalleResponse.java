package com.reservacanchas.dto.clip;

import com.reservacanchas.model.enums.EstadoClip;

import java.time.LocalDateTime;

public record ClipDetalleResponse(
        Long id,
        String urlVideo,
        String urlVideoFirmada,
        String urlMiniatura,
        int segundosAntes,
        int segundosDespues,
        Integer duracionSegundos,
        LocalDateTime instante,
        EstadoClip estado,
        boolean visible,
        int vistas,
        Long partidoId,
        Long grabacionId
) {
}

