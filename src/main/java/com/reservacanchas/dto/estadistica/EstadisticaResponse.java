package com.reservacanchas.dto.estadistica;

public record EstadisticaResponse(
        Long jugadorId,
        String jugadorNombre,
        int partidosJugados,
        int goles,
        int asistencias,
        double golesPorPartido,
        int victorias,
        int empates,
        int derrotas,
        double porcentajeVictorias,
        int participacionesEnGol
) {
}
