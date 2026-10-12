package com.reservacanchas.dto.sensor;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record EventoSensorRequest(
        @NotBlank(message = "El codigo del sensor es obligatorio")
        String codigoSensor,

        @NotBlank(message = "La clave API es obligatoria")
        String claveApi,

        LocalDateTime instante
) {
}

