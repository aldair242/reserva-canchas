package com.reservacanchas.controller;

import com.reservacanchas.dto.partido.InvitacionResponse;
import com.reservacanchas.service.InvitacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Invitaciones", description = "Endpoints publicos y privados de invitacion y vinculacion a partidos")
public class InvitacionController {

    private final InvitacionService invitacionService;

    public InvitacionController(InvitacionService invitacionService) {
        this.invitacionService = invitacionService;
    }

    @GetMapping("/{token}")
    @Operation(summary = "Obtener resumen publico de un partido por token", description = "Permite a cualquier usuario o invitado consultar datos generales del partido antes de unirse.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen del partido obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Token de invitacion no valido o expirado")
    })
    public ResponseEntity<InvitacionResponse> obtenerResumen(@PathVariable String token) {
        return ResponseEntity.ok(invitacionService.obtenerResumen(token));
    }

    @PostMapping("/{token}/reclamar")
    @Operation(summary = "Reclamar el perfil de un invitado", description = "Permite a un jugador registrado vincular a su cuenta las estadisticas y participaciones previas acumuladas como invitado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil reclamado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El perfil ya fue reclamado previamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Token de reclamo no encontrado")
    })
    public ResponseEntity<Void> reclamar(@PathVariable String token,
                                          Authentication authentication) {
        invitacionService.reclamarPerfil(token, authentication);
        return ResponseEntity.noContent().build();
    }
}
