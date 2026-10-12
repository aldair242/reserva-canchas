package com.reservacanchas.dto.estadistica;

public record RankingEntry(
        Long jugadorId,
        String jugadorNombre,
        int valor
) {
}
