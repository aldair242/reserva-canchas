package com.reservacanchas.dto.estadistica;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Entrada individual en la tabla de ranking o líderes")
public record RankingEntry(
        @Schema(description = "ID del jugador", example = "5")
        Long jugadorId,

        @Schema(description = "Nombre completo del jugador", example = "Lionel Messi")
        String jugadorNombre,

        @Schema(description = "Valor acumulado de la métrica (goles, asistencias o victorias)", example = "15")
        int valor
) {
}
