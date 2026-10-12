package com.reservacanchas.controller;

import com.reservacanchas.dto.partido.InvitacionResponse;
import com.reservacanchas.service.InvitacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invitaciones")
@Tag(name = "Invitaciones", description = "Endpoints de invitaciones a partidos")
public class InvitacionController {

    private final InvitacionService invitacionService;

    public InvitacionController(InvitacionService invitacionService) {
        this.invitacionService = invitacionService;
    }

    @GetMapping("/{token}")
    @Operation(summary = "Obtener resumen publico de un partido por token de invitacion")
    public ResponseEntity<InvitacionResponse> obtenerResumen(@PathVariable String token) {
        return ResponseEntity.ok(invitacionService.obtenerResumen(token));
    }

    @PostMapping("/{token}/reclamar")
    @Operation(summary = "Reclamar el perfil de un invitado (solo jugadores autenticados)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> reclamar(@PathVariable String token,
                                          Authentication authentication) {
        invitacionService.reclamarPerfil(token, authentication);
        return ResponseEntity.noContent().build();
    }
}
