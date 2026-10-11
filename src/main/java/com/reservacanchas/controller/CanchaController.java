package com.reservacanchas.controller;

import com.reservacanchas.dto.cancha.BloqueoCanchaRequest;
import com.reservacanchas.dto.cancha.CanchaRequest;
import com.reservacanchas.dto.cancha.CanchaResponse;
import com.reservacanchas.dto.cancha.DisponibilidadResponse;
import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.service.CanchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Canchas", description = "Endpoints de canchas deportivas")
@SecurityRequirement(name = "bearerAuth")
public class CanchaController {

    private final CanchaService canchaService;

    public CanchaController(CanchaService canchaService) {
        this.canchaService = canchaService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva cancha")
    public ResponseEntity<CanchaResponse> crear(@Valid @RequestBody CanchaRequest request,
                                                 Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(canchaService.crear(request, authentication));
    }

    @GetMapping
    @Operation(summary = "Listar canchas con filtros por deporte y sede")
    public ResponseEntity<List<CanchaResponse>> listarTodas(
            @Parameter(description = "Deporte para filtrar") @RequestParam(required = false) TipoDeporte deporte,
            @Parameter(description = "Id de la sede para filtrar") @RequestParam(required = false) Long sedeId) {
        return ResponseEntity.ok(canchaService.listarTodas(deporte, sedeId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una cancha por id")
    public ResponseEntity<CanchaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(canchaService.obtenerPorId(id));
    }

    @GetMapping("/{id}/disponibilidad")
    @Operation(summary = "Consultar disponibilidad de una cancha para una fecha")
    public ResponseEntity<DisponibilidadResponse> disponibilidad(
            @PathVariable Long id,
            @Parameter(description = "Fecha en formato yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(canchaService.obtenerDisponibilidad(id, fecha));
    }

    @PostMapping("/{id}/bloqueos")
    @Operation(summary = "Bloquear una cancha en un rango horario")
    public ResponseEntity<CanchaResponse> bloquear(@PathVariable Long id,
                                                    @Valid @RequestBody BloqueoCanchaRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(canchaService.bloquear(id, request, authentication));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una cancha")
    public ResponseEntity<CanchaResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody CanchaRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(canchaService.actualizar(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cancha")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        canchaService.eliminar(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
