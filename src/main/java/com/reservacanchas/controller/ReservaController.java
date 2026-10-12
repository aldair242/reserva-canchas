package com.reservacanchas.controller;

import com.reservacanchas.dto.reserva.CancelarRequest;
import com.reservacanchas.dto.reserva.PagoRequest;
import com.reservacanchas.dto.reserva.ReprogramarRequest;
import com.reservacanchas.dto.reserva.ReservaRequest;
import com.reservacanchas.dto.reserva.ReservaResponse;
import com.reservacanchas.service.ReservaService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Endpoints de reserva de canchas")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva (solo jugadores)")
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.crear(request, authentication));
    }

    @GetMapping("/mis")
    @Operation(summary = "Listar las reservas del jugador autenticado")
    public ResponseEntity<List<ReservaResponse>> listarMisReservas(Authentication authentication) {
        return ResponseEntity.ok(reservaService.listarMisReservas(authentication));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva propia")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id,
                                                     @Valid @RequestBody CancelarRequest request,
                                                     Authentication authentication) {
        return ResponseEntity.ok(reservaService.cancelar(id, request, authentication));
    }

    @PutMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar una reserva propia")
    public ResponseEntity<ReservaResponse> reprogramar(@PathVariable Long id,
                                                        @Valid @RequestBody ReprogramarRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(reservaService.reprogramar(id, request, authentication));
    }

    @PostMapping("/{id}/pagar")
    @Operation(summary = "Pagar una reserva propia")
    public ResponseEntity<ReservaResponse> pagar(@PathVariable Long id,
                                                  @Valid @RequestBody PagoRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(reservaService.pagar(id, request, authentication));
    }
}
