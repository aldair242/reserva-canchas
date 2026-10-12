package com.reservacanchas.controller;

import com.reservacanchas.dto.partido.AtribuirRequest;
import com.reservacanchas.dto.partido.EventoRequest;
import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Eventos", description = "Endpoints de gestion y arbitraje de eventos durante un partido (goles, tarjetas)")
@SecurityRequirement(name = "bearerAuth")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping("/api/partidos/{partidoId}/eventos")
    @Operation(summary = "Registrar manualmente un evento en un partido", description = "Permite registrar un gol, gol en contra o tarjeta en un partido en juego.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de evento invalidos o partido no en juego"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    })
    public ResponseEntity<EventoResponse> registrar(
            @PathVariable Long partidoId,
            @Valid @RequestBody EventoRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(eventoService.registrarEvento(partidoId, request, authentication));
    }

    @PutMapping("/api/eventos/{id}/atribuir")
    @Operation(summary = "Atribuir anotador y asistente de un gol", description = "Asigna el participante anotador y asistente a un gol en estado PENDIENTE_ATRIBUCION.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gol atribuido exitosamente"),
            @ApiResponse(responseCode = "400", description = "El evento no es un gol pendiente o participante invalido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<EventoResponse> atribuir(
            @PathVariable Long id,
            @RequestBody AtribuirRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(eventoService.atribuir(id, request, authentication));
    }

    @PutMapping("/api/eventos/{id}/confirmar")
    @Operation(summary = "Confirmar un evento", description = "Confirma un evento pendiente por un participante del equipo rival o administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento confirmado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El evento ya fue confirmado o estado no valido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El mismo usuario que atribuyo no puede auto-confirmar"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<EventoResponse> confirmar(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(eventoService.confirmar(id, authentication));
    }

    @PutMapping("/api/eventos/{id}/descartar")
    @Operation(summary = "Descartar un evento", description = "Descarta un evento no valido, excluyendolo del marcador y las estadisticas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento descartado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<EventoResponse> descartar(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(eventoService.descartar(id, authentication));
    }
}
