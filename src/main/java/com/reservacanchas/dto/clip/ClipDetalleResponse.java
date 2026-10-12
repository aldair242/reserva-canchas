package com.reservacanchas.dto.clip;

import com.reservacanchas.model.enums.EstadoClip;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle de clip de video con URL firmada para reproducción")
public record ClipDetalleResponse(
        @Schema(description = "ID del clip", example = "1")
        Long id,

        @Schema(description = "URL relativa o base del video", example = "/api/clips/archivos/clip_uuid.mp4")
        String urlVideo,

        @Schema(description = "URL temporal firmada con expiración para reproducción segura", example = "/api/clips/archivos/clip_uuid.mp4?exp=1791837492&sig=ab53ef...")
        String urlVideoFirmada,

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

        @Schema(description = "Indica si el clip es visible para los jugadores", example = "true")
        boolean visible,

        @Schema(description = "Contador de reproducciones", example = "4")
        int vistas,

        @Schema(description = "ID del partido asociado", example = "1")
        Long partidoId,

        @Schema(description = "ID de la grabación asociada", example = "1")
        Long grabacionId
) {
}

