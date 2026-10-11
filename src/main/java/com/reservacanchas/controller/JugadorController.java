package com.reservacanchas.controller;

import com.reservacanchas.dto.JugadorResponse;
import com.reservacanchas.service.JugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
@Tag(name = "Jugadores", description = "Endpoints de jugadores")
@SecurityRequirement(name = "bearerAuth")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los jugadores")
    public ResponseEntity<List<JugadorResponse>> listarTodos() {
        return ResponseEntity.ok(jugadorService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un jugador por id")
    public ResponseEntity<JugadorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(jugadorService.obtenerPorId(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener el perfil del jugador autenticado")
    public ResponseEntity<JugadorResponse> obtenerPerfilActual(Authentication authentication) {
        return ResponseEntity.ok(jugadorService.obtenerPerfilActual(authentication));
    }
}
