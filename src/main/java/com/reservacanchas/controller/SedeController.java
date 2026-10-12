package com.reservacanchas.controller;

import com.reservacanchas.dto.reserva.ReservaResponse;
import com.reservacanchas.dto.sede.SedeRequest;
import com.reservacanchas.dto.sede.SedeResponse;
import com.reservacanchas.service.SedeService;
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
@RequestMapping("/api/sedes")
@Tag(name = "Sedes", description = "Endpoints de administracion y consulta de sedes deportivas")
@SecurityRequirement(name = "bearerAuth")
public class SedeController {

    private final SedeService sedeService;

    public SedeController(SedeService sedeService) {
        this.sedeService = sedeService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva sede", description = "Registra una sede deportiva asignando su administrador y horarios. Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sede creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de sede invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo el rol ADMIN puede crear sedes"),
            @ApiResponse(responseCode = "404", description = "Administrador de sede especificado no existe")
    })
    public ResponseEntity<SedeResponse> crear(@Valid @RequestBody SedeRequest request,
                                               Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sedeService.crear(request, authentication));
    }

    @GetMapping
    @Operation(summary = "Listar todas las sedes", description = "Consulta publica de todas las sedes deportivas registradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de sedes obtenida exitosamente")
    })
    public ResponseEntity<List<SedeResponse>> listarTodas() {
        return ResponseEntity.ok(sedeService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una sede por id", description = "Consulta publica del detalle de una sede especifica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sede encontrada"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<SedeResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sedeService.obtenerPorId(id));
    }

    @GetMapping("/mis")
    @Operation(summary = "Listar las sedes del administrador autenticado", description = "Retorna las sedes bajo la administracion del usuario actual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de sedes del administrador obtenida"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo administradores de sede")
    })
    public ResponseEntity<List<SedeResponse>> listarMisSedes(Authentication authentication) {
        return ResponseEntity.ok(sedeService.listarMisSedes(authentication));
    }

    @GetMapping("/{id}/reservas")
    @Operation(summary = "Listar reservas de una sede por fecha", description = "Retorna las reservas de todas las canchas de la sede en la fecha indicada. Solo para su Administrador de Sede o ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de reservas de la sede"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<List<ReservaResponse>> listarReservasPorSede(
            @PathVariable Long id,
            @Parameter(description = "Fecha de consulta (YYYY-MM-DD)", required = false)
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        return ResponseEntity.ok(sedeService.listarReservasPorSede(id, fecha, authentication));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una sede", description = "Modifica direccion, horarios o estado de una sede existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sede actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualizacion invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para administrar esta sede"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<SedeResponse> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody SedeRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(sedeService.actualizar(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una sede", description = "Elimina una sede deportiva del sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sede eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        sedeService.eliminar(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
