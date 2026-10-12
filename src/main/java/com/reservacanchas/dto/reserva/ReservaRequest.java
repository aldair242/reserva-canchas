package com.reservacanchas.dto.reserva;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Solicitud de creacion de reserva de cancha")
public record ReservaRequest(
        @NotNull
        @Schema(description = "Id de la cancha deseada", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long canchaId,

        @NotNull
        @Schema(description = "Fecha de la reserva (yyyy-MM-dd)", example = "2026-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate fecha,

        @NotNull
        @Schema(description = "Hora de inicio del turno (HH:mm:ss)", example = "19:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalTime horaInicio,

        @NotNull
        @Schema(description = "Hora de fin del turno (HH:mm:ss)", example = "20:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalTime horaFin
) {
}
