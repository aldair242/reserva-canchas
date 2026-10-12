package com.reservacanchas.dto.sensor;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload enviado por el microcontrolador o sensor de arco al detectar un gol")
public record EventoSensorRequest(
        @Schema(description = "Código del sensor emisor", example = "SN-ARC-01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El codigo del sensor es obligatorio")
        String codigoSensor,

        @Schema(description = "Clave API secreta entregada al registrar el dispositivo", example = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La clave API es obligatoria")
        String claveApi,

        @Schema(description = "Instante exacto de detección del disparo/balón en el arco")
        LocalDateTime instante
) {
}

