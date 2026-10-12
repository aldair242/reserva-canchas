package com.reservacanchas.controller;

import com.reservacanchas.dto.partido.ParticipanteRequest;
import com.reservacanchas.dto.partido.PartidoResponse;
import com.reservacanchas.service.PartidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/partidos")
@Tag(name = "Partidos", description = "Endpoints de creacion, gestion de alineacion y ciclo de vida de partidos")
@SecurityRequirement(name = "bearerAuth")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    @PostMapping
    @Operation(summary = "Crear un partido a partir de una reserva confirmada y pagada", description = "Genera el registro del partido, asigna el token de invitacion y prepara las listas de participantes.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Partido generado exitosamente"),
            @ApiResponse(responseCode = "400", description = "La reserva no esta confirmada ni pagada, o ya tiene partido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No es el organizador de la reserva"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<PartidoResponse> crear(
            @Parameter(description = "Id de la reserva confirmada y pagada") @RequestParam Long reservaId,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partidoService.crearDesdeReserva(reservaId, authentication));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un partido por id", description = "Retorna el detalle completo del partido, participantes y marcador actual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Partido encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    })
    public ResponseEntity<PartidoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(partidoService.obtenerPorId(id));
    }

    @PostMapping("/{id}/participantes")
    @Operation(summary = "Agregar un participante al partido", description = "Incorpora a un jugador o invitado a uno de los equipos (A o B).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participante agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Capacidad del partido superada o datos invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Partido o persona no encontrada")
    })
    public ResponseEntity<PartidoResponse> agregarParticipante(
            @PathVariable Long id,
            @Valid @RequestBody ParticipanteRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(partidoService.agregarParticipante(id, request, authentication));
    }

    @PutMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar un partido", description = "Cambia el estado a EN_JUEGO, registra el inicioReal y activa la grabacion multimedia.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Partido iniciado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El partido no esta en estado PROGRAMADO"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    })
    public ResponseEntity<PartidoResponse> iniciar(@PathVariable Long id,
                                                    Authentication authentication) {
        return ResponseEntity.ok(partidoService.iniciar(id, authentication));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar un partido", description = "Cambia el estado a FINALIZADO, detiene la grabacion y recalcula el marcador final.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Partido finalizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El partido no esta EN_JUEGO"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    })
    public ResponseEntity<PartidoResponse> finalizar(@PathVariable Long id,
                                                      Authentication authentication) {
        return ResponseEntity.ok(partidoService.finalizar(id, authentication));
    }
}
