package com.reservacanchas.controller;

import com.reservacanchas.dto.estadistica.EstadisticaResponse;
import com.reservacanchas.service.EstadisticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/jugadores")
@Tag(name = "Estadisticas", description = "Endpoints de rendimiento y metricas deportivas de jugadores")
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping("/{id}/estadisticas")
    @Operation(summary = "Obtener estadisticas de un jugador", description = "Calcula partidos jugados, goles, asistencias, victorias, derrotas y promedios con filtros opcionales de sede y rango de fechas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estadisticas calculadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Jugador no encontrado")
    })
    public ResponseEntity<EstadisticaResponse> obtenerEstadisticas(
            @PathVariable Long id,
            @Parameter(description = "Id opcional de sede para filtrar") @RequestParam(required = false) Long sedeId,
            @Parameter(description = "Fecha inicial de filtro yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final de filtro yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(estadisticaService.calcularEstadisticas(id, sedeId, desde, hasta));
    }
}
