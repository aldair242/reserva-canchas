package com.reservacanchas.dto.estadistica;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen estadístico individual acumulado de un jugador")
public record EstadisticaResponse(
        @Schema(description = "ID del jugador", example = "5")
        Long jugadorId,

        @Schema(description = "Nombre completo del jugador", example = "Lionel Messi")
        String jugadorNombre,

        @Schema(description = "Total de partidos disputados", example = "20")
        int partidosJugados,

        @Schema(description = "Total de goles marcados", example = "15")
        int goles,

        @Schema(description = "Total de asistencias realizadas", example = "8")
        int asistencias,

        @Schema(description = "Promedio de goles por partido", example = "0.75")
        double golesPorPartido,

        @Schema(description = "Partidos ganados", example = "14")
        int victorias,

        @Schema(description = "Partidos empatados", example = "3")
        int empates,

        @Schema(description = "Partidos perdidos", example = "3")
        int derrotas,

        @Schema(description = "Porcentaje de victorias (0-100)", example = "70.0")
        double porcentajeVictorias,

        @Schema(description = "Suma de goles y asistencias", example = "23")
        int participacionesEnGol
) {
}
