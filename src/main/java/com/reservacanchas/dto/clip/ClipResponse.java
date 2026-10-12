package com.reservacanchas.dto.clip;

import com.reservacanchas.model.enums.EstadoClip;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de clip de video generado para un partido")
public record ClipResponse(
        @Schema(description = "ID del clip", example = "1")
        Long id,

        @Schema(description = "URL relativa o recurso del video", example = "/api/clips/archivos/clip_uuid.mp4")
        String urlVideo,

        @Schema(description = "URL de la imagen miniatura", example = "/api/clips/archivos/thumb_uuid.jpg")
        String urlMiniatura,

        @Schema(description = "Segundos previos al evento capturados", example = "15")
        int segundosAntes,

        @Schema(description = "Segundos posteriores al evento capturados", example = "8")
        int segundosDespues,

        @Schema(description = "Duración total del clip en segundos", example = "23")
        Integer duracionSegundos,

        @Schema(description = "Instante en que ocurrió la jugada")
        LocalDateTime instante,

        @Schema(description = "Estado de procesamiento del clip", example = "LISTO")
        EstadoClip estado,

        @Schema(description = "Indica si el clip es visible para los participantes", example = "true")
        boolean visible,

        @Schema(description = "Contador de visualizaciones", example = "3")
        int vistas,

        @Schema(description = "ID del partido asociado", example = "1")
        Long partidoId,

        @Schema(description = "ID de la grabación asociada", example = "1")
        Long grabacionId
) {
}

