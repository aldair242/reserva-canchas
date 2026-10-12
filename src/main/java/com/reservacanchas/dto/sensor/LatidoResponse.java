package com.reservacanchas.dto.sensor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del latido de un dispositivo")
public record LatidoResponse(
        @Schema(description = "Código del dispositivo confirmado", example = "SN-ARC-01")
        String codigo,

        @Schema(description = "Fecha y hora registrada para el latido")
        LocalDateTime ultimoLatido,

        @Schema(description = "Indica si el dispositivo está actualmente considerado en línea", example = "true")
        boolean enLinea
) {
}

