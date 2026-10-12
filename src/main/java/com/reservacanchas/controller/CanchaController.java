package com.reservacanchas.controller;

import com.reservacanchas.dto.cancha.BloqueoCanchaRequest;
import com.reservacanchas.dto.cancha.CanchaRequest;
import com.reservacanchas.dto.cancha.CanchaResponse;
import com.reservacanchas.dto.cancha.DisponibilidadResponse;
import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.service.CanchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/canchas")
@Tag(name = "Canchas", description = "Endpoints de gestion, consulta y disponibilidad de canchas deportivas")
@SecurityRequirement(name = "bearerAuth")
public class CanchaController {

    private final CanchaService canchaService;

    public CanchaController(CanchaService canchaService) {
        this.canchaService = canchaService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva cancha", description = "Registra una cancha deportiva con su subtipo y estrategia tarifaria. Requiere rol ADMIN o ADMIN_SEDE de la sede correspondiente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cancha creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para la sede indicada"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<CanchaResponse> crear(@Valid @RequestBody CanchaRequest request,
                                                 Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(canchaService.crear(request, authentication));
    }

    @GetMapping
    @Operation(summary = "Listar canchas con filtros por deporte y sede", description = "Retorna el catalogo de canchas publicas, con filtros opcionales por tipo de deporte y por sede.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de canchas obtenida exitosamente")
    })
    public ResponseEntity<List<CanchaResponse>> listarTodas(
            @Parameter(description = "Deporte para filtrar") @RequestParam(required = false) TipoDeporte deporte,
            @Parameter(description = "Id de la sede para filtrar") @RequestParam(required = false) Long sedeId) {
        return ResponseEntity.ok(canchaService.listarTodas(deporte, sedeId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una cancha por id", description = "Retorna el detalle completo de una cancha deportiva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cancha encontrada"),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada")
    })
    public ResponseEntity<CanchaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(canchaService.obtenerPorId(id));
    }

    @GetMapping("/{id}/disponibilidad")
    @Operation(summary = "Consultar disponibilidad de una cancha para una fecha", description = "Retorna los bloqueos programados y los turnos horarios disponibles para la cancha en la fecha consultada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidad consultada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada")
    })
    public ResponseEntity<DisponibilidadResponse> disponibilidad(
            @PathVariable Long id,
            @Parameter(description = "Fecha en formato yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(canchaService.obtenerDisponibilidad(id, fecha));
    }

    @PostMapping("/{id}/bloqueos")
    @Operation(summary = "Bloquear una cancha en un rango horario", description = "Permite al administrador inhabilitar la cancha en un rango de fecha/hora por mantenimiento u otro motivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cancha bloqueada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas invalido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para administrar la cancha"),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada")
    })
    public ResponseEntity<CanchaResponse> bloquear(@PathVariable Long id,
                                                    @Valid @RequestBody BloqueoCanchaRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(canchaService.bloquear(id, request, authentication));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una cancha", description = "Actualiza caracteristicas, deporte o tarifas de una cancha existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cancha actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualizacion invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para administrar la cancha"),
            @ApiResponse(responseCode = "404", description = "Cancha o sede no encontrada")
    })
    public ResponseEntity<CanchaResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody CanchaRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(canchaService.actualizar(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cancha", description = "Elimina una cancha del sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cancha eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        canchaService.eliminar(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
