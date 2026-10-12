package com.reservacanchas.controller;

import com.reservacanchas.dto.reserva.CancelarRequest;
import com.reservacanchas.dto.reserva.PagoRequest;
import com.reservacanchas.dto.reserva.ReprogramarRequest;
import com.reservacanchas.dto.reserva.ReservaRequest;
import com.reservacanchas.dto.reserva.ReservaResponse;
import com.reservacanchas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Endpoints de creacion, cancelacion, reprogramacion y pago de reservas de canchas")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva", description = "Crea una reserva en estado PENDIENTE bloqueando concurrentemente la cancha para evitar solapamientos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Horario invalido o fuera del horario de atencion"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "409", description = "La cancha ya esta reservada en ese horario o en proceso de reserva"),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada")
    })
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.crear(request, authentication));
    }

    @GetMapping("/mis")
    @Operation(summary = "Listar las reservas del jugador autenticado", description = "Retorna el historial y estado de todas las reservas creadas por el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<ReservaResponse>> listarMisReservas(Authentication authentication) {
        return ResponseEntity.ok(reservaService.listarMisReservas(authentication));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva propia", description = "Cancela la reserva si faltan al menos 2 horas para su inicio.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Faltan menos de 2 horas o el estado no permite cancelacion"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No es el organizador de la reserva"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id,
                                                     @Valid @RequestBody CancelarRequest request,
                                                     Authentication authentication) {
        return ResponseEntity.ok(reservaService.cancelar(id, request, authentication));
    }

    @PutMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar una reserva propia", description = "Cambia la fecha u horario de una reserva previa validando disponibilidad con bloqueo pesimista.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva reprogramada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de reprogramacion invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No es el organizador de la reserva"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "409", description = "La cancha ya esta ocupada en el nuevo horario")
    })
    public ResponseEntity<ReservaResponse> reprogramar(@PathVariable Long id,
                                                        @Valid @RequestBody ReprogramarRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(reservaService.reprogramar(id, request, authentication));
    }

    @PostMapping("/{id}/pagar")
    @Operation(summary = "Pagar una reserva propia", description = "Registra y procesa el pago segun la modalidad elegida (tarjeta, billetera, transferencia o efectivo).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago procesado y reserva confirmada"),
            @ApiResponse(responseCode = "400", description = "Estado de reserva no valido para pago o datos de pago invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No es el organizador de la reserva"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaResponse> pagar(@PathVariable Long id,
                                                  @Valid @RequestBody PagoRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(reservaService.pagar(id, request, authentication));
    }
}
