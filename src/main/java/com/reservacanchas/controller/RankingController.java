package com.reservacanchas.controller;

import com.reservacanchas.dto.estadistica.RankingEntry;
import com.reservacanchas.service.EstadisticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@Tag(name = "Rankings", description = "Endpoints de tablas de clasificacion y lideres estadisticos")
public class RankingController {

    private final EstadisticaService estadisticaService;

    public RankingController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping
    @Operation(summary = "Obtener ranking de jugadores por criterio", description = "Genera el ranking ordenado por goles, asistencias o victorias, con filtros opcionales de sede y fecha.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking calculado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Criterio de ranking desconocido")
    })
    public ResponseEntity<List<RankingEntry>> ranking(
            @Parameter(description = "Criterio de ordenamiento: GOLES, ASISTENCIAS o VICTORIAS") @RequestParam String criterio,
            @Parameter(description = "Id de sede para filtrar") @RequestParam(required = false) Long sedeId,
            @Parameter(description = "Fecha desde yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha hasta yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(estadisticaService.ranking(criterio, sedeId, desde, hasta));
    }
}
