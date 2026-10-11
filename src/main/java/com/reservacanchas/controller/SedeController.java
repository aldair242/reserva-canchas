package com.reservacanchas.controller;

import com.reservacanchas.dto.sede.SedeRequest;
import com.reservacanchas.dto.sede.SedeResponse;
import com.reservacanchas.service.SedeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
@Tag(name = "Sedes", description = "Endpoints de sedes deportivas")
@SecurityRequirement(name = "bearerAuth")
public class SedeController {

    private final SedeService sedeService;

    public SedeController(SedeService sedeService) {
        this.sedeService = sedeService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva sede")
    public ResponseEntity<SedeResponse> crear(@Valid @RequestBody SedeRequest request,
                                               Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sedeService.crear(request, authentication));
    }

    @GetMapping
    @Operation(summary = "Listar todas las sedes")
    public ResponseEntity<List<SedeResponse>> listarTodas() {
        return ResponseEntity.ok(sedeService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una sede por id")
    public ResponseEntity<SedeResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sedeService.obtenerPorId(id));
    }

    @GetMapping("/mis")
    @Operation(summary = "Listar las sedes del administrador de sede autenticado")
    public ResponseEntity<List<SedeResponse>> listarMisSedes(Authentication authentication) {
        return ResponseEntity.ok(sedeService.listarMisSedes(authentication));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una sede")
    public ResponseEntity<SedeResponse> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody SedeRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(sedeService.actualizar(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una sede")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        sedeService.eliminar(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
