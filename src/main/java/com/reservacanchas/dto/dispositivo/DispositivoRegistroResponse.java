package com.reservacanchas.dto.dispositivo;

import com.reservacanchas.model.enums.UbicacionSensor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de registro inicial de un dispositivo (incluye la clave API en texto plano solo en esta respuesta)")
public record DispositivoRegistroResponse(
        @Schema(description = "ID del dispositivo", example = "1")
        Long id,

        @Schema(description = "Tipo de dispositivo", example = "SENSOR")
        String tipo,

        @Schema(description = "Código identificador único del dispositivo", example = "SN-ARC-01")
        String codigo,

        @Schema(description = "Clave API en texto claro (generada una única vez, guárdela en un lugar seguro)", example = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6")
        String claveApi,

        @Schema(description = "Indica si el dispositivo está activo", example = "true")
        boolean activo,

        @Schema(description = "ID de la cancha asignada", example = "1")
        Long canchaId,

        @Schema(description = "Ubicación del sensor (para sensores de arco)", example = "ARCO_NORTE")
        UbicacionSensor ubicacion,

        @Schema(description = "Sensibilidad configurada (para sensores)", example = "85")
        Integer sensibilidad,

        @Schema(description = "URL del stream RTSP o HTTP (para cámaras)", example = "rtsp://camera.local/live")
        String urlStream,

        @Schema(description = "Resolución del video (para cámaras)", example = "1080p")
        String resolucion,

        @Schema(description = "Fotogramas por segundo (para cámaras)", example = "30")
        Integer fps
) {
}

