package com.reservacanchas.dto.dispositivo;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para registrar una cámara en una cancha")
public record RegistroCamaraRequest(
        @Schema(description = "Código identificador de la cámara", example = "CAM-CANCHA-01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El codigo es obligatorio")
        String codigo,

        @Schema(description = "URL RTSP o HTTP de transmisión de video", example = "rtsp://camera1.local/live")
        String urlStream,

        @Schema(description = "Resolución de video", example = "1080p")
        String resolucion,

        @Schema(description = "Fotogramas por segundo", example = "30")
        Integer fps
) {
}

