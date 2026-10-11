package com.reservacanchas.dto.cancha;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BloqueoCanchaRequest(
        @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fin,
        String motivo
) {
}
