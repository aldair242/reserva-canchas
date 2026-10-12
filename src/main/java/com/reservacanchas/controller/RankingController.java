package com.reservacanchas.controller;

import com.reservacanchas.dto.estadistica.RankingEntry;
import com.reservacanchas.service.EstadisticaService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Rankings", description = "Endpoints de rankings de jugadores")
public class RankingController {

    private final EstadisticaService estadisticaService;

    public RankingController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping
    @Operation(summary = "Obtener ranking de jugadores por criterio")
    public ResponseEntity<List<RankingEntry>> ranking(
            @RequestParam String criterio,
            @RequestParam(required = false) Long sedeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(estadisticaService.ranking(criterio, sedeId, desde, hasta));
    }
}
