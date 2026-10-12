package com.reservacanchas.dto.dispositivo;

import com.reservacanchas.model.enums.UbicacionSensor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información pública/administrativa de un dispositivo (sin revelar hashes ni claves)")
public record DispositivoResponse(
        @Schema(description = "ID del dispositivo", example = "1")
        Long id,

        @Schema(description = "Tipo de dispositivo", example = "SENSOR")
        String tipo,

        @Schema(description = "Código del dispositivo", example = "SN-ARC-01")
        String codigo,

        @Schema(description = "Fecha y hora del último latido registrado")
        LocalDateTime ultimoLatido,

        @Schema(description = "Indica si el dispositivo está habilitado administrativamente", example = "true")
        boolean activo,

        @Schema(description = "Indica si el dispositivo envió un latido en los últimos 2 minutos", example = "true")
        boolean enLinea,

        @Schema(description = "ID de la cancha", example = "1")
        Long canchaId,

        @Schema(description = "Nombre de la cancha asignada", example = "Cancha Principal")
        String canchaNombre,

        @Schema(description = "Ubicación del sensor de arco (si aplica)", example = "ARCO_NORTE")
        UbicacionSensor ubicacion,

        @Schema(description = "Sensibilidad del sensor (si aplica)", example = "80")
        Integer sensibilidad,

        @Schema(description = "URL de streaming de la cámara (si aplica)", example = "rtsp://camera.local/live")
        String urlStream,

        @Schema(description = "Resolución de la cámara (si aplica)", example = "1080p")
        String resolucion,

        @Schema(description = "FPS de la cámara (si aplica)", example = "30")
        Integer fps
) {
}

