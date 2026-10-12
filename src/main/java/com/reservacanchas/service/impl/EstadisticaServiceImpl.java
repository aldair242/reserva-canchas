package com.reservacanchas.service.impl;

import com.reservacanchas.dto.estadistica.EstadisticaResponse;
import com.reservacanchas.dto.estadistica.RankingEntry;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.repository.EventoPartidoRepository;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.repository.ParticipantePartidoRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.service.EstadisticaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstadisticaServiceImpl implements EstadisticaService {

    private final ParticipantePartidoRepository participanteRepository;
    private final EventoPartidoRepository eventoRepository;
    private final JugadorRepository jugadorRepository;
    private final PartidoRepository partidoRepository;

    public EstadisticaServiceImpl(ParticipantePartidoRepository participanteRepository,
                                  EventoPartidoRepository eventoRepository,
                                  JugadorRepository jugadorRepository,
                                  PartidoRepository partidoRepository) {
        this.participanteRepository = participanteRepository;
        this.eventoRepository = eventoRepository;
        this.jugadorRepository = jugadorRepository;
        this.partidoRepository = partidoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticaResponse calcularEstadisticas(Long jugadorId, Long sedeId,
                                                     LocalDate desde, LocalDate hasta) {
        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador", jugadorId));

        List<ParticipantePartido> participaciones = participanteRepository.findPartidosJugados(
                jugadorId, EstadoPartido.FINALIZADO, sedeId, desde, hasta);

        int partidosJugados = participaciones.size();
        int victorias = 0;
        int empates = 0;
        int derrotas = 0;

        for (ParticipantePartido pp : participaciones) {
            Partido partido = pp.getPartido();
            LadoEquipo ganador = partido.obtenerGanador();
            if (ganador == null) {
                empates++;
            } else if (ganador == pp.getEquipo()) {
                victorias++;
            } else {
                derrotas++;
            }
        }

        int goles = (int) eventoRepository.contarGoles(
                EstadoEvento.CONFIRMADO, jugadorId, sedeId, desde, hasta);
        int asistencias = (int) eventoRepository.contarAsistencias(
                EstadoEvento.CONFIRMADO, jugadorId, sedeId, desde, hasta);

        double golesPorPartido = partidosJugados > 0 ? (double) goles / partidosJugados : 0.0;
        double porcentajeVictorias = (victorias + empates + derrotas) > 0
                ? (double) victorias / (victorias + empates + derrotas) * 100.0 : 0.0;

        return new EstadisticaResponse(
                jugador.getId(),
                jugador.getNombreVisible(),
                partidosJugados,
                goles,
                asistencias,
                golesPorPartido,
                victorias,
                empates,
                derrotas,
                porcentajeVictorias,
                goles + asistencias
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingEntry> ranking(String criterio, Long sedeId,
                                      LocalDate desde, LocalDate hasta) {
        return switch (criterio.toLowerCase()) {
            case "goles" -> rankingGoles(sedeId, desde, hasta);
            case "asistencias" -> rankingAsistencias(sedeId, desde, hasta);
            case "victorias" -> rankingVictorias(sedeId, desde, hasta);
            default -> throw new IllegalArgumentException(
                    "Criterio de ranking no valido: " + criterio);
        };
    }

    private List<RankingEntry> rankingGoles(Long sedeId, LocalDate desde, LocalDate hasta) {
        List<Object[]> resultados = eventoRepository.rankingGoles(
                EstadoEvento.CONFIRMADO, sedeId, desde, hasta);
        return mapearRanking(resultados);
    }

    private List<RankingEntry> rankingAsistencias(Long sedeId, LocalDate desde, LocalDate hasta) {
        List<Object[]> resultados = eventoRepository.rankingAsistencias(
                EstadoEvento.CONFIRMADO, sedeId, desde, hasta);
        return mapearRanking(resultados);
    }

    private List<RankingEntry> rankingVictorias(Long sedeId, LocalDate desde, LocalDate hasta) {
        Map<Long, Integer> victoriasPorJugador = new HashMap<>();
        List<Partido> partidos = partidoRepository.findAll();
        for (Partido partido : partidos) {
            if (partido.getEstado() != EstadoPartido.FINALIZADO) {
                continue;
            }
            if (sedeId != null && !partido.getReserva().getCancha().getSede().getId().equals(sedeId)) {
                continue;
            }
            if (desde != null && partido.getReserva().getFecha().isBefore(desde)) {
                continue;
            }
            if (hasta != null && partido.getReserva().getFecha().isAfter(hasta)) {
                continue;
            }
            LadoEquipo ganador = partido.obtenerGanador();
            if (ganador == null) {
                continue;
            }
            for (ParticipantePartido pp : partido.getParticipantes()) {
                if (pp.getEquipo() == ganador && pp.getPersona() instanceof Jugador j) {
                    victoriasPorJugador.merge(j.getId(), 1, Integer::sum);
                }
            }
        }
        List<RankingEntry> ranking = new ArrayList<>();
        victoriasPorJugador.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry -> {
                    Jugador j = jugadorRepository.findById(entry.getKey()).orElse(null);
                    if (j != null) {
                        ranking.add(new RankingEntry(j.getId(), j.getNombreVisible(), entry.getValue()));
                    }
                });
        return ranking;
    }

    private List<RankingEntry> mapearRanking(List<Object[]> resultados) {
        List<RankingEntry> ranking = new ArrayList<>();
        for (Object[] fila : resultados) {
            Long jugadorId = (Long) fila[0];
            Long valor = (Long) fila[1];
            Jugador j = jugadorRepository.findById(jugadorId).orElse(null);
            if (j != null) {
                ranking.add(new RankingEntry(j.getId(), j.getNombreVisible(), valor.intValue()));
            }
        }
        return ranking;
    }
}
