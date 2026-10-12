package com.reservacanchas.dto.dispositivo;

import jakarta.validation.constraints.NotBlank;

public record RegistroCamaraRequest(
        @NotBlank(message = "El codigo es obligatorio")
        String codigo,

        String urlStream,
        String resolucion,
        Integer fps
) {
}

