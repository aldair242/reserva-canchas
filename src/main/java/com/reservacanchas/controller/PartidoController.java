package com.reservacanchas.controller;

import com.reservacanchas.dto.partido.ParticipanteRequest;
import com.reservacanchas.dto.partido.PartidoResponse;
import com.reservacanchas.service.PartidoService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Partidos", description = "Endpoints de gestion de partidos")
@SecurityRequirement(name = "bearerAuth")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    @PostMapping
    @Operation(summary = "Crear un partido a partir de una reserva confirmada y pagada")
    public ResponseEntity<PartidoResponse> crear(@RequestParam Long reservaId,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partidoService.crearDesdeReserva(reservaId, authentication));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un partido por id")
    public ResponseEntity<PartidoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(partidoService.obtenerPorId(id));
    }

    @PostMapping("/{id}/participantes")
    @Operation(summary = "Agregar un participante al partido")
    public ResponseEntity<PartidoResponse> agregarParticipante(
            @PathVariable Long id,
            @Valid @RequestBody ParticipanteRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(partidoService.agregarParticipante(id, request, authentication));
    }

    @PutMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar un partido")
    public ResponseEntity<PartidoResponse> iniciar(@PathVariable Long id,
                                                    Authentication authentication) {
        return ResponseEntity.ok(partidoService.iniciar(id, authentication));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar un partido")
    public ResponseEntity<PartidoResponse> finalizar(@PathVariable Long id,
                                                      Authentication authentication) {
        return ResponseEntity.ok(partidoService.finalizar(id, authentication));
    }
}
