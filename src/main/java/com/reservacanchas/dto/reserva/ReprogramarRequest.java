package com.reservacanchas.dto.reserva;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Solicitud de reprogramacion de fecha u horario de una reserva")
public record ReprogramarRequest(
        @NotNull
        @Schema(description = "Nueva fecha para la reserva (yyyy-MM-dd)", example = "2026-10-25", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate fecha,

        @NotNull
        @Schema(description = "Nueva hora de inicio (HH:mm:ss)", example = "20:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalTime horaInicio,

        @NotNull
        @Schema(description = "Nueva hora de fin (HH:mm:ss)", example = "21:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalTime horaFin
) {
}
