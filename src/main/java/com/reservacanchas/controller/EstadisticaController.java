package com.reservacanchas.controller;

import com.reservacanchas.dto.estadistica.EstadisticaResponse;
import com.reservacanchas.service.EstadisticaService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Estadisticas", description = "Endpoints de estadisticas de jugadores")
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping("/{id}/estadisticas")
    @Operation(summary = "Obtener estadisticas de un jugador")
    public ResponseEntity<EstadisticaResponse> obtenerEstadisticas(
            @PathVariable Long id,
            @RequestParam(required = false) Long sedeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(estadisticaService.calcularEstadisticas(id, sedeId, desde, hasta));
    }
}
