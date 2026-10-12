package com.reservacanchas.dto.reserva;

import jakarta.validation.constraints.NotBlank;

public record CancelarRequest(
        @NotBlank String motivo
) {
}
