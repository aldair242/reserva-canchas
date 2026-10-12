package com.reservacanchas.service;

import com.reservacanchas.dto.estadistica.EstadisticaResponse;
import com.reservacanchas.dto.estadistica.RankingEntry;

import java.time.LocalDate;
import java.util.List;

public interface EstadisticaService {

    EstadisticaResponse calcularEstadisticas(Long jugadorId, Long sedeId,
                                              LocalDate desde, LocalDate hasta);

    List<RankingEntry> ranking(String criterio, Long sedeId, LocalDate desde, LocalDate hasta);
}
