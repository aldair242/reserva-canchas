package com.reservacanchas.dto.cancha;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Solicitud para registrar un bloqueo de horario en una cancha")
public record BloqueoCanchaRequest(
        @NotNull
        @Schema(description = "Fecha y hora de inicio del bloqueo", example = "2026-10-15T08:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime inicio,

        @NotNull
        @Schema(description = "Fecha y hora de finalizacion del bloqueo", example = "2026-10-15T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime fin,

        @Schema(description = "Motivo del bloqueo (mantenimiento, evento privado, etc.)", example = "Mantenimiento de cesped sintetico")
        String motivo
) {
}
