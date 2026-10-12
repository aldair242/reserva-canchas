package com.reservacanchas.controller;

import com.reservacanchas.dto.partido.AtribuirRequest;
import com.reservacanchas.dto.partido.EventoRequest;
import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Tag(name = "Eventos", description = "Endpoints de eventos de partidos")
@SecurityRequirement(name = "bearerAuth")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping("/api/partidos/{partidoId}/eventos")
    @Operation(summary = "Registrar manualmente un evento en un partido")
    public ResponseEntity<EventoResponse> registrar(
            @PathVariable Long partidoId,
            @Valid @RequestBody EventoRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(eventoService.registrarEvento(partidoId, request, authentication));
    }

    @PutMapping("/api/eventos/{id}/atribuir")
    @Operation(summary = "Atribuir anotador y asistente de un gol")
    public ResponseEntity<EventoResponse> atribuir(
            @PathVariable Long id,
            @RequestBody AtribuirRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(eventoService.atribuir(id, request, authentication));
    }

    @PutMapping("/api/eventos/{id}/confirmar")
    @Operation(summary = "Confirmar un evento (por un participante distinto del atribuidor)")
    public ResponseEntity<EventoResponse> confirmar(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(eventoService.confirmar(id, authentication));
    }

    @PutMapping("/api/eventos/{id}/descartar")
    @Operation(summary = "Descartar un evento")
    public ResponseEntity<EventoResponse> descartar(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(eventoService.descartar(id, authentication));
    }
}
